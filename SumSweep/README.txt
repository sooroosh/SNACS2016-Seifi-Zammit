This is a graph library that provides some graph operations.
To run it, it is necessary to provide the following arguments:

-p (--operation) (COMPULSORY): the operation to perform. The allowed operations 
                               are:
    - help: displays this help file;
    - sumsweepundir: runs the SumSweep algorithm on the undirected graph provided
		with argument -i.
    - sumsweepdir: runs the SumSweep algorithm on the directed graph provided
		with argument -i.
    - sumsweepdirscc: runs the SumSweep algorithm on the biggest SCC of the 
		directed graph provided	with argument -i.
    - convertascii: converts all graphs stored in the webgraph folder into ascii
                    graphs (only for readability).
    - convertwebgraph: converts all graphs stored in the ascii folder into 
                       webgraphs (used to generate inputs).

-i (--input) (COMPULSORY ONLY FOR OPERATIONS sumsweepdir, sumsweepdirscc, 
	sumsweepundir): the filename of the input graph. It must be stored in
	webgraph format in folder inputWebgraph/Directed/ for sumsweepdir and 
	sumsweepdirscc, in folder inputWebgraph/Undirected for sumsweepundir.

-h (--help) (OPTIONAL): overrides all other options and displays this help file.

-v (--verbose) (OPTIONAL): sets the default verbosity, according to the 
                           following scale:
    - 0: no output is provided;
    - 1: only the results of the computation are provided (default);
    - 2: also partial results obtained during the algorithm are provided.

Examples:	java -jar SumSweep.jar -p sumsweepdir -i as-caida20071105 -v 1
		java -jar SumSweep.jar -p sumsweepundir -i as20000102 -v 2
		java -jar SumSweep.jar -p convertwebgraph
