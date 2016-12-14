import sys
import csv
import networkx as nx
import time
import os
import gc

NDE_EXTENSION = '.nde'
NDE_DELIMITER = ' '
TXT_DELIMITER = '\t'
COMMENT_PREFIX = '#'
NEW_LINE = os.linesep

f = False	# file to open
G = False	# graph to build from parsed file

def error(line_num, err_msg, given=''):
	global f, G

	sys.stderr.write(NEW_LINE)
	if line_num < 0:
		sys.stderr.write(err_msg)
	else:
		sys.stderr.write('line ' + str(line_num) + ': ' + err_msg + ' [' + given + ']')
	sys.stderr.write(NEW_LINE + NEW_LINE)
	if f:
		f.close()
	if G:
		G.clear()
		del(G)	
	sys.exit(-1)

#
# parse command line args
#
if not len(sys.argv) == 3 or not sys.argv[1].lower().endswith(NDE_EXTENSION) or \
not (sys.argv[2].lower() == 'undirected' or sys.argv[2].lower() == 'directed'):
	error(-1, sys.argv[0] + ': <' + NDE_EXTENSION + ' file> <undirected | directed> args expected')

#
# parse directed/undirected NDE file -> edge list
#
is_directed = sys.argv[2].lower() == 'directed'
line_cnt = 1
preamble = 0
with open(sys.argv[1], 'r') as f:
	reader = csv.reader(f, delimiter = NDE_DELIMITER)

	# parse preamble
	for line in reader:
		if line[0].startswith(COMMENT_PREFIX):
			preamble += 1
		else:
			break;

	# get number of nodes
	num_nodes = 0
	num_edges = 0
	largest_node_num = -1

	try:
		num_nodes = int(line[0])
	except:
		pass
	if not len(line) == 1 or num_nodes <= 0:
		error(preamble + line_cnt, 'valid number of nodes expected', NDE_DELIMIMTER.join(line))

	line_cnt += 1


	# skip degrees sub-part, but do basic checks and total number of edges
	for line in reader:
		num_fields = 0
		this_node = -1
		this_degrees = -1
		try:
			num_fields = len(line)
			this_node = int(line[0])
			this_degrees = int(line[1])
			num_edges += this_degrees
		except:
			pass
		if not num_fields == 2 or not this_node == line_cnt-2 or not this_degrees >= 0:
			error(preamble + line_cnt, 'valid node - degree count expected', NDE_DELIMITER.join(line))	

		line_cnt += 1

		if line_cnt == num_nodes+2:
			break

	if not is_directed:
		num_edges /= 2


	# parse edge list
	if is_directed:
		G = nx.DiGraph()
	else:
		G = nx.Graph()

	for line in reader:
		num_fields = 0
		source_node = -1
		target_node = -1
		try:
			num_fields = len(line)
			source_node = int(line[0])
			target_node = int(line[1])
			G.add_edge(source_node, target_node)

			if (max(source_node, target_node > largest_node_num)):
				largest_node_num = max(source_node, target_node)
		except:
			pass
		if not num_fields == 2 or source_node < 0 or target_node < 0:
			# forbid self-loops? ->  or source_node == target_node:
			error(preamble + line_cnt, 'valid source, target nodes expected', NDE_DELIMITER.join(line))

		line_cnt += 1

	gc.collect()

	g_num_nodes = largest_node_num + 1
	g_num_edges = G.number_of_edges()

	if not num_nodes == g_num_nodes:
		sys.stderr.write('WARNING: in ' + sys.argv[1]  + ' number of nodes parsed is ' + str(g_num_nodes) + ' [' + str(num_nodes) + ' declared]' + NEW_LINE)

	if not num_edges == g_num_edges:
		sys.stderr.write('WARNING: in ' + sys.argv[1] + ' number of edges parsed is ' + str(g_num_edges) + ' [' + str(num_edges) + ' declared]' + NEW_LINE)

	
	# dump edge list
	sys.stdout.write('# source: ' + sys.argv[1] + NEW_LINE)
	sys.stdout.write('# gen by: ' + sys.argv[0] + NEW_LINE)
	sys.stdout.write('# gen dt: ' + time.strftime("%c") + NEW_LINE + '#' + NEW_LINE)
	sys.stdout.write('# num nodes: ' + str(g_num_nodes) + NEW_LINE)
	sys.stdout.write('# num edges: ' + str(g_num_edges) + NEW_LINE)

#	for node, nbrdict in G.adjacency_iter():
#		for nbr in sorted(nbrdict):
#			sys.stdout.write(str(node) + TXT_DELIMITER + str(nbr) + NEW_LINE)

	for e in sorted(G.edges_iter()):
		sys.stdout.write(str(e[0]) + TXT_DELIMITER + str(e[1]) + NEW_LINE)

	f.close()
	G.clear()
	del(G)
	gc.collect()
