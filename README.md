# SNACS2016-Seifi-Zammit
Diameter Computation
This Repository consists of the source codes for the approaches discussed by Alan Zammit and Soroush Seifi in their report for Social Network Analysis project. 

The code for each individual approach (SumSweep,Bounding Diameters (teexgraph),Magnien(diam),lasagne) are taken from the internet and belong to their respectful authors of the corresponding papers. Furthermore, there is brief instructions for each of these approaches for somebody who is interested to repeat the experiments. However, the reader should note the following in order to be able to repeat the experiments without the problem :

1. Approaches denoted by the names magnien (diam) and lasagne use NDE files as inputs. 
2. The input file for the diam (magnien) approach can be stored in the same directory or any sub-directory of the source code.
3. The input file for the lasagne should be stored in a folder called NETWORKS at the same directory level as its jar file.
4. SumSweep requires input data files (in webgraph format) to be kept in a subdirectory inputWebgraph of its own, containing subdirectories Directed and Undirected.
4. teexgraph requires data to be located in the same directoy or any sub-directory of the source code and in edge list format

-------------------------------------------------------------------------------------------------
The other code scripts such as d.run.py and the scripts included in parsing folder belong to this experimental study and are detailed as folloows.

1- Parsing folder: 
Since different approaches discussed in this study use different graph formats as input files, this folder consists of two python scripts used in order to convert two graph formats used in the approaches.

nde2edge.py converts NDE format (compatible with both magnien/lasagne) to teexgraph/sumsweep while edge2nde.py does the reverse conversion.

example:
python nde2edge.py ip.nde undirected|directed > ip.txt
python edge2nde ip.txt > ip.nde


It is suggested to keep an NDE version of all parsed files under a folder called NETWORKS to be used by both magnien and lasange while keeping an edge list version of all parsed files under NETWORKS/inputAscii which gets translated by SumSweep.jar into the appropriate webgraph format under NETWORKS/inputWebgraph


2- d.run.py is an automated script which uses the source codes of all the approaches above and tests them on different datasets and outputs the results for each approach and each dataset. One could easily change the datasets used by editing the hardcoded datasets in the  d.run.config file and including the dataset itself in the NETWORKS folder as instructed above.
This script can be executed using the following command:

python d.run.py d.run.config


The folder FutureWorks consists of the source code of the proposed method for generating synthetic graph using pyevoleve. One could easily change the eval_func and choose another fitness measure for evolving graphs. To run the code set the settings in the test.py and run:

python test.py


