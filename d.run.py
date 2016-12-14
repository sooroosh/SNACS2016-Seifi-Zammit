import sys
import os
import imp
from subprocess import Popen, PIPE
import glob
from ntpath import basename
from shutil import copy2

HOME_LN = 0
HOME_PREFIX = 'HOME='

ACTION_CHAR = '='
SEP_CHAR = ','
CLEAN_ACT = 'CLEAN'
SOURCE_ACT = 'SOURCE'
EXEC_ACT = 'EXEC'

yes = 'yes'

COMMENT_CHAR = '#'
NEW_LINE = os.linesep
TAB_CHAR = '\t'
ALL_FILES = '*'

TXT_EXTENSION = '.txt'
NDE_EXTENSION = '.nde'

def error(err_msg):
	sys.stderr.write(err_msg + NEW_LINE + NEW_LINE)
	sys.stderr.flush()
	sys.exit(-1)

def out(msg):
	sys.stdout.write(msg)
	sys.stdout.flush()

def is_exe(fpath):
        return os.path.isfile(fpath) and os.access(fpath, os.X_OK)

# check arg
if not len(sys.argv) == 2 or not os.access(sys.argv[1], os.R_OK):
	error(NEW_LINE + 'usage: ' + sys.argv[0] + ' <config file>')

# parse config file
num_comments = 0
with open(sys.argv[1], 'r') as f:
	config = f.readlines()

	if len(config) == 0:
		error(NEW_LINE + 'empty config file')

	out(NEW_LINE)

	while (config[num_comments].startswith(COMMENT_CHAR) or \
		len(config[num_comments]) <= 1):
		num_comments += 1

	# set HOME directory for tests; CWD by default
	if config[HOME_LN+num_comments].upper().startswith(HOME_PREFIX):
		home = config[HOME_LN+num_comments][len(HOME_PREFIX):-1]
		if not os.path.isdir(home):
			home = os.getcwd()
		if not home[-1:] == os.sep:
			home += os.sep
		out('- setting ' + HOME_PREFIX + home + NEW_LINE)
	else:
		error(NEW_LINE + 'line ' + str(HOME_LN+num_comments) + ': ' + HOME_PREFIX + ' expected')

	# check dependencies and env
	out(NEW_LINE + '- checking environment and dependenices:' + NEW_LINE + NEW_LINE)

	out('python version 2.7...')
	if sys.version_info[0] == 2 and sys.version_info[1] >= 7:
		out('yes' + NEW_LINE)
	else:
		error('NO')

	out('networkx...')
	try:
		imp.find_module('networkx')
		out('yes' + NEW_LINE)
	except ImportError:
		error('NO')

	out('JRE...')
	p = Popen(['java', '-version'], stdout=PIPE, stderr=PIPE, stdin=PIPE)
	if p.stderr.read().lower().startswith('java version'):
		out('yes' + NEW_LINE)
	else:
		error('NO')

	out('[SNACS tools and directory structure]' + NEW_LINE)

	IAD = 'NETWORKS/inputAscii/Directed/'
	out(TAB_CHAR + IAD + '...')
	IAD = home + IAD
	if os.path.isdir(IAD):
		out('yes' + NEW_LINE)
	else:
		error('NO')

	IAU = 'NETWORKS/inputAscii/Undirected/'
	out(TAB_CHAR + IAU + '...')
	IAU = home + IAU
	if os.path.isdir(IAU):
		out('yes' + NEW_LINE)
	else:
		error('NO')

	IND = 'NETWORKS/inputNDE/'
	out(TAB_CHAR + IND + '...')
	IND = home + IND
	if os.path.isdir(IND):
		out('yes' + NEW_LINE)
	else:
		error('NO')

	ITX = 'NETWORKS/inputTxt/'
	out(TAB_CHAR + ITX + '...')
	ITX = home + ITX
	if os.path.isdir(ITX):
		out('yes' + NEW_LINE)
	else:
		error('NO')

	IWD = 'NETWORKS/inputWebgraph/Directed/'
	out(TAB_CHAR + IWD + '...')
	IWD = home + IWD
	if os.path.isdir(IWD):
		out('yes' + NEW_LINE)
	else:
		error('NO')

	IWU = 'NETWORKS/inputWebgraph/Undirected/'
	out(TAB_CHAR + IWU + '...')
	IWU = home + IWU
	if os.path.isdir(IWU):
		out('yes' + NEW_LINE)
	else:
		error('NO')

	MAG = 'magnien/per_diam'
	out(TAB_CHAR + MAG + '...')
	MAG = home + MAG
	if is_exe(MAG):
		out('yes' + NEW_LINE)
	else:
		error('NO')

	LAS = 'per_lasagne'
	out(TAB_CHAR + LAS + '...')
	LAS = home + LAS 
	if is_exe(LAS):
		out('yes' + NEW_LINE)
	else:
		error('NO')

	TEE = 'teexgraph/teexgraph'
	out(TAB_CHAR + TEE + '...')
	TEE = home + TEE 
	if is_exe(TEE):
		out('yes' + NEW_LINE)
	else:
		error('NO')

	SUM = 'SumSweep/SumSweep.jar'
	out(TAB_CHAR + SUM + '...')
	SUM = home + SUM 
	if is_exe(SUM):
		out('yes' + NEW_LINE)
	else:
		error('NO')

	E2N = 'parsing/edge2nde.py'
	out(TAB_CHAR + E2N + '...')
	E2N = home + E2N 
	if is_exe(E2N):
		out('yes' + NEW_LINE)
	else:
		error('NO')

	N2E = 'parsing/nde2edge.py'
	out(TAB_CHAR + N2E + '...')
	N2E = home + N2E 
	if is_exe(N2E):
		out('yes' + NEW_LINE)
	else:
		error('NO')

	out('done.' + NEW_LINE + NEW_LINE + \
		'- performing tasks:' + NEW_LINE + NEW_LINE)

	sources = [] # list of data sources on which we execute algos

	# perform required tasks
	task_num = HOME_LN + num_comments + 1
	while True:
		while ((task_num < len(config)) and (config[task_num].startswith(COMMENT_CHAR) or \
			len(config[task_num]) <= 1)):
			task_num += 1

		if (task_num < len(config)):	
			this_task = config[task_num][:-1]
			act_pos = this_task.find(ACTION_CHAR)
			if act_pos > 0:
				params = this_task[act_pos+1:]
				action = this_task[:act_pos].upper()

				if action == CLEAN_ACT:
					if params.lower() == yes:
						out('cleaning data dirs...')
						removed_ok = 0
						try:	
							for fn in glob.glob(IAD + ALL_FILES):
								os.remove(fn)
								removed_ok += 1
						except OSError:
							pass
						try:
							for fn in glob.glob(IAU + ALL_FILES):
								os.remove(fn)
								removed_ok += 1
						except OSError:
							pass
						try:
							for fn in glob.glob(IND + ALL_FILES):
								os.remove(fn)
								removed_ok += 1
						except OSError:
							pass
						try:
							for fn in glob.glob(ITX + ALL_FILES):
								os.remove(fn)
								removed_ok += 1
						except OSError:
							pass
						try:
							for fn in glob.glob(IWD + ALL_FILES):
								os.remove(fn)
								removed_ok += 1
						except OSError:
							pass
						try:
							for fn in glob.glob(IWU + ALL_FILES):
								os.remove(fn)
								removed_ok += 1
						except OSError:
							pass
						out(str(removed_ok) + ' files deleted' + NEW_LINE)

				elif action == SOURCE_ACT:

					out('sourcing ' + params + '...')

					first = True

					if params.lower().endswith(TXT_EXTENSION):
						for txt_file in glob.glob(home + params):
							if first:
								first = False
							else:
								out(SEP_CHAR)

							out(basename(txt_file))
	
							# copy to inputTxt
							copy2(txt_file, ITX)

							# convert TXT to NDE and deposit in inputNDE subdir
							nde_file = open(IND+basename(txt_file)[:-len(TXT_EXTENSION)]+NDE_EXTENSION, 'w+')
							p = Popen(['python', home + 'parsing/edge2nde.py', txt_file], stdout=nde_file, stderr=PIPE, stdin=PIPE)
							p.wait() # finished?
							nde_file.flush()
							nde_file.close()

							# copy to inputAscii/Undirected
							copy2(txt_file, IAU)
						
							# convert into webgraph
							p = Popen(['java', '-jar', 'SumSweep.jar', '-p', 'convertwebgraph'], cwd=home + 'SumSweep', stdout=PIPE, stderr=PIPE, stdin=PIPE)
							p.wait() # finished?
						out(NEW_LINE)
						sources.append(params)

					if params.lower().endswith(NDE_EXTENSION):
						for nde_file in glob.glob(home + params):
							if first:
								first = False
							else:
								out(SEP_CHAR)

							out(basename(nde_file))

							# copy to inputNDE
							copy2(nde_file, IND)
							
							# convert NDE to TXT and deposit in inputTxt subdir
							txt_fn = ITX+basename(nde_file)[:-len(NDE_EXTENSION)]+TXT_EXTENSION
							txt_file = open(txt_fn, 'w+')
							p = Popen(['python', home + 'parsing/nde2edge.py', nde_file, 'undirected'], stdout=txt_file, stderr=PIPE, stdin=PIPE)
							p.wait() # finished?
							txt_file.flush()
							txt_file.close()

							# copy newly createx TXT file to inputAsciiUndirected
							copy2(txt_fn, IAU)

							# convert into webgraph
							p = Popen(['java', '-jar', 'SumSweep.jar', '-p', 'convertwebgraph'], cwd=home + 'SumSweep', stdout=PIPE, stderr=PIPE, stdin=PIPE)
							p.wait() # finished?
						out(NEW_LINE)
						sources.append(params)

				elif action == EXEC_ACT:

					if not len(sources) > 0:
						error('need to build data sources first to execute ' + params)

					exec_params = params.split(SEP_CHAR)
					for source in sources:
						out('executing on...')
						first = True
						for file in glob.glob(home + source):
							if first:
								first = False
							else:
								out(SEP_CHAR)

							this_file = os.path.splitext(basename(file))[0]
							out(this_file)

							if len(exec_params) > 0:
								out('[')

							firstExec = True
							for e in exec_params:
								if firstExec:
									firstExec = False
								else:
									out('; ')

								if e.lower() == 'magnien':
									p = Popen(['./per_diam', IND + this_file + NDE_EXTENSION, '10', '1'], cwd=home + 'magnien', stdout=PIPE, stderr=PIPE, stdin=PIPE)
									p.wait() # finished?
									mag_out = p.stdout.read().split(NEW_LINE)[-2].split(' ')	# get last non-empty line of magnien output
									mag_iter = mag_out[0]
									mag_lb = mag_out[7]
									mag_ub = mag_out[8]
									out('magnien=' + mag_iter + SEP_CHAR + mag_lb + SEP_CHAR + mag_ub)

								elif e.lower() == 'lasagne':
									p = Popen(['./per_lasagne', IND + this_file + NDE_EXTENSION, '10'], cwd=home, stdout=PIPE, stderr=PIPE, stdin=PIPE)
									p.wait()
									ignore = p.stderr.read()	# ? needs stderr to be siphoned to work!?
									las_out = p.stdout.read()
									las_out = las_out.split(NEW_LINE)[-3]	# the line before (non-empty) last has diameter
									out('lasagne=' + las_out.split(' ')[2])

								elif e.lower() == 'teexgraph':
									p = Popen(['./teexgraph', ITX + this_file + TXT_EXTENSION], cwd=home + 'teexgraph', stdout=PIPE, stderr=PIPE, stdin=PIPE)
									p.wait()
									tee_out = p.stderr.read().split(' ')
									tee_iter = tee_out[-13].split(NEW_LINE)[0].split(TAB_CHAR)[1]

									tee_out = p.stdout.read().split(NEW_LINE)
									tee_diam = tee_out[0]
									out('teexgraph=' + tee_iter + SEP_CHAR + tee_diam)
									
								elif e.lower() == 'sumsweep':
									p = Popen(['java', '-jar', 'SumSweep.jar', '-p', 'sumsweepundir', '-i', this_file], cwd=home + 'SumSweep', stdout=PIPE, stderr=PIPE, stdin=PIPE)
									p.wait() # finished?
									sum_out = p.stdout.read().split(NEW_LINE)[-3]
									sum_iter = sum_out.split(' ')[-2][1:]
									sum_diam = sum_out.split(' ')[1]
									out('sumsweep=' + sum_iter + SEP_CHAR + sum_diam)

							if len(exec_params) > 0:
								out(']')
						out(NEW_LINE)
			else:
				error('action expected at line ' + str(task_num+1) + ' [' + this_task + ']')
			task_num += 1
		else:
			break

out(NEW_LINE + 'done.' + NEW_LINE + NEW_LINE)
