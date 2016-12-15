import networkx as nx
import pyevolve.G1DList
import graph_genome
import random
import cPickle

def sanity_checks(genome):
    sanity_check_out_edges(genome)
    sanity_check_node_parameters(genome)
    sanity_check_number_nodes(genome)
    sanity_check_node_zero(genome)
        
def sanity_check_out_edges(genome):
    '''
    Check that every node has the right number of out_edges and with the
    right action number
    @param tester:
    @param genome:
    '''
    nodes_degrees = genome.node_degrees
    
    for node, n_dict in genome.graph.nodes_iter(data=True):
        edges_pool = set()
        type_id = n_dict["type_id"]
        required_out_degree = nodes_degrees[type_id]
        if not required_out_degree == genome.graph.out_degree(node):
            return False
        for _, _ , e_dict in genome.graph.out_edges_iter(node, data=True):
            edges_pool.add( e_dict["action_number"] )
        if not len(edges_pool) == required_out_degree:
            return False
    return True

def sanity_check_number_nodes(genome):
    '''
    Check that every graph has at least 2 nodes
    @param tester:
    @param genome:
    '''
    return len(genome.graph) >= 2

def sanity_check_node_parameters(genome):
    '''
    Check that every node has the right number of parameters
    @param tester:
    @param genome:
    '''
    node_params = genome.node_params
    
    for node, n_dict in genome.graph.nodes_iter(data=True):        
        params = n_dict["parameters"]
        if not isinstance(params,pyevolve.G1DList.G1DList):
            return False
        type_id = n_dict["type_id"]
        if not len(params) == node_params[type_id]:
            return False
    return True

def sanity_check_node_zero(genome):
    '''
    Starting node
    @param tester:
    @param genome:
    '''
    
    node = genome[genome.starting_node]

def sanity_check_all_connected(genome):
    
    tree = nx.dfs_tree(genome.graph, source=genome.starting_node)
    
    if len(tree) > 2:
        return len(tree) == len(genome.graph)
