Clemence Magnien and Matthieu Latapy
September 2007
http://www-rp.lip6.fr/~magnien/Diameter
clemence.magnien@lip6.fr

This README file describes succintly the program computing
bounds for the diameter massive graphs, provided at the
webpage above.

The program is a main C source file 'diam.c', together with
a file 'prelim.c'. It is provided 'as is' with no warranty.
You may use and distribute it, provided you cite the web page
above, and the paper:
 Fast Computation of Empirically Tight Bounds
  for the Diameter of Massive Graphs
 Clأ©mence Magnien, Matthieu Latapy, and Michel Habib,
 ACM Journal of Experimental Algorithmics (JEA), 13, 2009.
(A preprint is available from the web page above.)
The program is designed for Linux/Unix systems but may be
compiled on any 'reasonable' platform. See below.

Please write us an e-mail if you find it useful, if you find
some bugs or have any idea to improve it.


* QUICKSTART *
**************
1. Compile the program:

  gcc -O3 diam.c -o diam

2. Run it:

 ./diam -diam nb_max difference < data_file
  for computing bounds for the diameter for 'nb_max' steps
  or until the difference between the best obtained bounds
  is equal to or lower than 'difference'.

or

 ./diam -prec nb_max precision < data_file
  for computing bounds for the diameter for 'nb_max' steps
  or until the diameter is estimated with a relative error
  of at most 'precision'.

* INPUT FORMAT *
****************
The program reads plain text ; the first line must be the number n
of nodes ; then comes a series of lines of the form 'i j' meaning
that node 'i' has degree 'j', and then a series of lines of the
form 'u v' meaning that nodes 'u' and 'v' are linked together.
There *must* be no duplicate lines, and 'u v' also stands for 'v u'.
The nodes must be numbered from 0 to n-1. There must be no loop
'u u'. The program makes basic verifications but may crash or give
wrong answers if the input is incorrect.
Example :
3
0 2
1 2
2 2
0 1
0 2
2 1
(3 nodes, thus numbered from 0 to 2, node 0 has degree 2, node 1
has degree 2, and node 2 has degree 2 too, and the links are 0 1,
0 2 and 2 1)


* COMMAND LINE OPTIONS *
************************

The standard way of using the program is to compute both upper and 
lower bounds for the diameter, until the difference between them is
lesser than a given value, or until a given number of iterations
have been run:

 ./diam -diam nb_max difference
  computes 'nb_max' iterations of both a double-sweep lower bound and
  a highest degree tree upper bound (see the paper for more details).
  If the difference between the best upper and lower bounds obtained
  is lesser than or equal to 'difference' at a given step before
  'nb_max' steps have been done, the program stops.

An alternative is to compute bounds for the diameter until the diameter
is estimated with a given relative error, or until a given number of
iterations have been run:

 ./diam -prec nb_max precision
  computes 'nb_max' iterations of both a double-sweep lower bound and
  a highest degree tree upper bound (see the paper for more details).
  If the the best upper and lower bounds obtained are such that the
  diameter is estimated with a relative error of at most 'precision'
  at a given step before 'nb_max' steps have been done, the program
  stops.


Moreover, it is possible to compute a given number of iterations of
specific bounds (see the paper for more details on the different
bounds):

 ./diam -tlb|dslb|tub|rtub|hdtub nb [deg_begin]
  computes 'nb' iterations of one specific bound:
  -tlb: trivial lower bound
  -dslb: double-sweep lower bound
  -tub: trivial upper bound
  -rtub: random tree upper bound
  -hdtub: highest degree tree upper bound (this last option requires
    an additional parameter deg_begin, which skips nodes with degrees
    greater than deg_begin. 0 means start with the highest degree
    node).



* OUTPUT *
**********
The program writes the results on the standard output. Thus,
if you want to save them you should redirect it, using
typically './diam -diam 100 0 < data_file > result_file'.
Moreover, the program writes some information on what it is
doing on the standard error output. You may want to discard
this or redirect it to a file.

The output format consists of one line per iteration, providing
the following informations:
 - iteration number
 - number of node chosen for computing the bound
 - degree of this node
 - value of the obtained bound.

Additionnally, for the -diam option, the values of the best bounds
obtained so far are provided.

The first line of the output specifies which column provides which
information.

* PORTABILITY *
***************
The program is written in ANSI C, with standard libraries.


