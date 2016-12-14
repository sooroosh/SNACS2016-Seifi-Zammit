import sys
import csv
import networkx as nx
import time
import os
import gc


TXT_EXTENSION = '.txt'
TXT_DELIMITER = '\t'
NDE_DELIMITER = ' '
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
if not len(sys.argv) == 2 or not sys.argv[1].lower().endswith(TXT_EXTENSION):
	error(-1, sys.argv[0] + ': <' + TXT_EXTENSION + 'file> arg expected')

#
# parse directed/undirected NDE file -> edge list
#
line_cnt = 1
with open(sys.argv[1], 'r') as f:
	reader = csv.reader(f, delimiter = TXT_DELIMITER)

	preamble = 0
	largest_node_num = -1
	smallest_node_num = sys.maxint
	G = nx.DiGraph()

	# parse preamble 
	for line in reader:
		if line[0].startswith(COMMENT_PREFIX):
			preamble += 1
		else:
			break;

	# parse edge list
	while True:
		num_fields = 0
		source_node = -1
		target_node = -1
		try:
			num_fields = len(line)
			source_node = int(line[0])
			target_node = int(line[1])

			if not source_node == target_node:
				G.add_edge(source_node, target_node)

			if max(source_node, target_node) > largest_node_num:
				largest_node_num = max(source_node, target_node)

			if min(source_node, target_node) < smallest_node_num:
				smallest_node_num = min(source_node, target_node)
		except:
			pass
		if not num_fields == 2 or source_node < 0 or target_node < 0:
			# forbid self-loops? -> or source_node == target_node:
			error(preamble + line_cnt, 'valid source, target nodes expected', TXT_DELIMITER.join(line))	

		line_cnt += 1

		try:
			line = reader.next()
		except StopIteration:
			break;


	# dump header (comments) 
	g_num_nodes = largest_node_num - smallest_node_num + 1
	g_num_edges = G.number_of_edges()

	sys.stdout.write('# source: ' + sys.argv[1] + NEW_LINE)
	sys.stdout.write('# gen by: ' + sys.argv[0] + NEW_LINE)
	sys.stdout.write('# gen dt: ' + time.strftime("%c") + NEW_LINE + '#' + NEW_LINE)
	sys.stdout.write('# num edges: ' + str(g_num_edges) + NEW_LINE)

	# issue nodes part:
	sys.stdout.write(str(g_num_nodes) + NEW_LINE)

	# issue degrees part:
	last_node_num = -1
	for node in sorted(G.nodes_iter()):
		out_cnt = G.out_degree(node)
		in_cnt = G.in_degree(node)
	
		node -= smallest_node_num		# rebase to node 0

		if last_node_num == -1:
			last_node_num = node
		elif node-last_node_num > 1:
			filler = node-last_node_num-1
			for x in range(0, filler):
				last_node_num += 1	# need contigious numbers in degree part - backfill spaces if necessary
				sys.stdout.write(str(last_node_num) + NDE_DELIMITER + '0' + NEW_LINE)
		sys.stdout.write(str(node) + NDE_DELIMITER + str(in_cnt + out_cnt) + NEW_LINE)
		last_node_num = node

	# issue edges part:
	for e in sorted(G.edges_iter()):
		sys.stdout.write(str(e[0]-smallest_node_num) + NDE_DELIMITER \
		+ str(e[1]-smallest_node_num) + NEW_LINE)

	f.close()
	G.clear()
	del(G)
