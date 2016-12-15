from pyevolve import GSimpleGA
from pyevolve import G1DList
from pyevolve import Selectors
from pyevolve import Initializators, Mutators,  Consts
from pyevolve import DBAdapters
from pyevolve import Crossovers

import graph_genome
import networkx as nx
import matplotlib.pyplot as plt
from sanity_checks import sanity_checks
import math

def eval_func2(chromosome, **args):
	G = chromosome.graph
	centr = nx.closeness_centrality(G)
	c_sum = sum(centr.values())
	p_sum = sum([node.params[0] for node in chromosome.nodes]) / len(G)
	return c_sum * p_sum

def eval_func(chromosome, **args):
	G = chromosome.graph
	return nx.average_clustering(nx.Graph(G))

def step_checks(ga_engine):
	for genome in ga_engine.getPopulation():
		sanity_checks(genome)

out_degrees=[1,2,3]
node_params=[0,0,0]
genome = graph_genome.GraphGenome(25, out_degrees, node_params)
genome.evaluator.set(eval_func2)
genome.setParams(p_del=0.5, p_add=0.5)
        
ga = GSimpleGA.GSimpleGA(genome)
ga.setElitism(True)
        
ga.selector.set(Selectors.GRouletteWheel)
ga.getPopulation().setParams(tournamentPool = 500)
        
ga.setGenerations(10000)
ga.setPopulationSize(500)
ga.setCrossoverRate(0.78)
ga.setMutationRate(0.08)
#ga.setMinimax(Consts.minimaxType["maximize"])
ga.setMinimax(Consts.minimaxType["minimize"])
ga.stepCallback.set(step_checks)

ga.evolve(freq_stats = 10)

gp = ga.getPopulation()
for i in gp:
	print (i.fitness)

best = ga.bestIndividual()
print ("best: ", best)
print (best.getFitnessScore())

G = best.graph
pos=nx.spring_layout(G)
nx.draw_networkx_nodes(G,pos)
nx.draw_networkx_edges(G,pos)
nx.draw_networkx_labels(G,pos)
plt.axis('off')

plt.show()
