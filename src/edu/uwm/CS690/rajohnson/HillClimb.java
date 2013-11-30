package edu.uwm.CS690.rajohnson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class HillClimb {

	private Heuristic h;
	
	public HillClimb(Heuristic heur) {
		h = heur;
	}
	
	/**
	 * Starting from {@code startingState} find the best food selection that exceeds 95%
	 * @param startingState The starting location for this hill climbing search.
	 * @return The best solution that could be found from the starting state.
	 */
	public FoodSelectionNode findMaximumFromStartState(FoodSelectionNode startingState)
	{
		/*System.out.println("Starting state: " + startingState);
		System.out.println("\t" + startingState.totalNutrientsAndCostAsString());*/
		ArrayList<FoodSelectionNode> startingNeighbors = generateNeighbors(startingState);
		FoodSelectionNode previousBest = startingState;
		FoodSelectionNode nextBestNeighbor = findBestNeighbor(startingNeighbors);
		
		HashSet<FoodSelectionNode> visitedNodes = new HashSet<FoodSelectionNode>();
		visitedNodes.add(startingState);
		
		/*System.out.println("nextBestNeighbor state: " + nextBestNeighbor);
		System.out.println("\t" + nextBestNeighbor.totalNutrientsAndCostAsString());*/
		
		visitedNodes.add(nextBestNeighbor);
		
		// While the nutrients don't exceed 95% or the nextBestNeighbor is better than the previous best state 
		while((!previousBest.totalNutrientsExceeds95Percent()) || (h.calculateHeuristic(previousBest) >=
			   h.calculateHeuristic(nextBestNeighbor)))
		{
			
			ArrayList<FoodSelectionNode> currentNeighbors = generateNeighbors(nextBestNeighbor);
			currentNeighbors = findUnvisitedNeighbors(currentNeighbors, visitedNodes);
			previousBest = nextBestNeighbor;
			nextBestNeighbor = findBestNeighbor(currentNeighbors);
			/*System.out.println("nextBestNeighbor state: " + nextBestNeighbor);
			System.out.println("\t" + nextBestNeighbor.totalNutrientsAndCostAsString());*/
			
			visitedNodes.add(nextBestNeighbor);
		}
		
		return previousBest;
	}
	
	/**
	 * Takes the current state of the search and generates all neighbors. These neighbors are the states where one of each food item has been added
	 * or one existing food choice has been decremented by one.
	 * @param currentState The current state of the search.
	 * @return An {@code ArrayList} of all the neighbor states as defined above. 
	 */
	private ArrayList<FoodSelectionNode> generateNeighbors(FoodSelectionNode currentState)
	{
		ArrayList<FoodSelectionNode> neighbors = new ArrayList<FoodSelectionNode>();
		int[] currentChoiceArray = currentState.getFoodChosen();
		for(int i = 0; i < currentChoiceArray.length; i++)
		{
			int[] currentStateClone = (int[])currentChoiceArray.clone();
			// Choose to include this neighbor
			currentStateClone[i]++;
			neighbors.add(new FoodSelectionNode(currentStateClone));
			
			// If there already is a non-zero amount of food item i+1 in the currentState, 
			// also choose to include an instance where this food choice is taken away.
			if(currentChoiceArray[i] > 0)
			{
				currentStateClone = (int[])currentChoiceArray.clone();
				currentStateClone[i]--;
				neighbors.add(new FoodSelectionNode(currentStateClone));
			}
		}
		
		return neighbors;
	}

	/**
	 * Finds the neighbor {@link FoodSelectionNode} which returns the lowest value according to {@link Heuristic} {@code h}.
	 * The lower the value generated by the {@link Heuristic}, the closer the node is to the goal. 
	 * @param currentNeighbors An {@code ArrayList} of {@link FoodSelectionNode}s that are the neighbors of the current state.
	 * @return The {@link FoodSelectionNode} which is closest to the solution according to the {@link Heuristic} {@code h}.
	 */
	private FoodSelectionNode findBestNeighbor(ArrayList<FoodSelectionNode> currentNeighbors)
	{
		return Collections.min(currentNeighbors, new NodeComparator<FoodSelectionNode>());
	}
	
	/**
	 * A comparator that compares {@link FoodSelectionNode}s.
	 * @author Reed Johnson
	 * @date 11.29.2013
	 * @param <T> A generic type. This is expected to be type {@link FoodSelectionNode}. If the types of
	 * {@code arg0} or {@code arg1} are not {@link FoodSelectionNode}, then they are assumed to be equal.
	 * Attempt to use this for a type other than {@link FoodSelectionNode} at your own risk.   
	 */
	private class NodeComparator<T> implements Comparator<T>
	{

		@Override
		public int compare(T arg0, T arg1) {
			if(arg0 == null)
			{
				return -1;
			}
			else if(arg1 == null)
			{
				return 1;
			}
			else if((arg0 instanceof FoodSelectionNode) && (arg1 instanceof FoodSelectionNode))
			{
				return Integer.compare(h.calculateHeuristic((FoodSelectionNode) arg0),
						h.calculateHeuristic(((FoodSelectionNode) arg1)));
			}
			else
			{
				return 0;
			}
		}
		
	}
	
	private ArrayList<FoodSelectionNode> findUnvisitedNeighbors(ArrayList<FoodSelectionNode> generatedNeighbors, HashSet<FoodSelectionNode> visitedNodes)
	{
		ArrayList<FoodSelectionNode> unvisitedNeighbors = new ArrayList<FoodSelectionNode>(generatedNeighbors);
		for(int i = 0;i < unvisitedNeighbors.size();)
		{
			//If the match checked exists in the DB already, then remove it, else iterate again
			if(visitedNodes.contains(unvisitedNeighbors.get(i)))
			{
				unvisitedNeighbors.remove(i);
			}
			else
			{
				i++;
			}
		}
		
		return unvisitedNeighbors;
	}
}