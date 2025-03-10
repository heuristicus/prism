//==============================================================================
//	
//	Copyright (c) 2014-
//	Authors:
//	* Joachim Klein <klein@tcs.inf.tu-dresden.de> (TU Dresden)
//	
//------------------------------------------------------------------------------
//	
//	This file is part of PRISM.
//	
//	PRISM is free software; you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation; either version 2 of the License, or
//	(at your option) any later version.
//	
//	PRISM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with PRISM; if not, write to the Free Software Foundation,
//	Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//	
//==============================================================================


package explicit;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import prism.PrismException;
//import prism.PrismNotSupportedException;
import explicit.rewards.MDPRewards;
import explicit.rewards.MDPRewardsSimple;

/**
 * Base class for the results of a product operation between a model and
 * an automaton. Provides infrastructure for converting information on the
 * states between the original model, the automaton and the product model.
 *
 * The mapping between product states and the constituent parts is
 * specified via the (abstract) functions getModelState() and getAutomatonState().
 *
 * @param <M> The type of the product model, e.g, DTMC, MDP, ...
 */
public abstract class Product<M extends Model<?>> implements ModelTransformation<M, M>
{
	protected M originalModel = null;
	protected M productModel = null;

	/**
	 * For the given productState index, return the corresponding
	 * state index in the original model.
	 */
	public abstract int getModelState(int productState);

	/**
	 * For the given productState index, return the corresponding
	 * state index in the automaton.
	 */
	public abstract int getAutomatonState(int productState);

	/**
	 * Return the number of states in the automaton.
	 */
	public abstract int getAutomatonSize();

	/**
	 * Constructor.
 	 * @param originalModel the original model
	 */
	public Product(M originalModel)
	{
		this.originalModel = originalModel;
	}

	/**
	 * Constructor.
 	 * @param productModel the product model
 	 * @param originalModel the original model
	 */
	public Product(M productModel, M originalModel)
	{
		this.originalModel = originalModel;
		this.productModel = productModel;
	}

	/**
	 * Get the product model.
	 */
	public M getProductModel()
	{
		return productModel;
	}

	@Override
	public M getTransformedModel()
	{
		return getProductModel();
	}

	@Override
	public M getOriginalModel()
	{
		return originalModel;
	}

	/**
	 * Lifts a BitSet over states in the automaton to a BitSet
	 * over states in the product model: A bit is set in the
	 * result for a product state if the bit is set in the parameter
	 * for the corresponding automaton state.
	 * @param automataStates a BitSet over states of the automaton for this product.
	 */
	public BitSet liftFromAutomaton(BitSet automataStates)
	{
		BitSet result = new BitSet();

		for (int productState = 0; productState < productModel.getNumStates(); productState++) {
			if (automataStates.get(getAutomatonState(productState))) {
				result.set(productState, true);
			}
		}

		return result;
	}

	/**
	 * Lifts a BitSet over states in the original model to a BitSet
	 * over states in the product model: A bit is set in the
	 * result for a product state if the bit is set in the parameter
	 * for the corresponding original model state.
	 * @param modelStates a BitSet over states of the original model for this product.
	 */
	public BitSet liftFromModel(BitSet modelStates)
	{
		BitSet result = new BitSet();

		for (int productState = 0; productState < productModel.getNumStates(); productState++) {
			if (modelStates.get(getModelState(productState))) {
				result.set(productState, true);
			}
		}

		return result;
	}

	/**
	 * Calculate the progression reward from the automaton distance metric
	 * @param distsToAcc List of automaton distances to goal
	 * @return the product mdp progression reward
	 */	
	public MDPRewards liftProgressionFromAutomaton(List<Double> distsToAcc)
	{
		MDP productMDP = (MDP)productModel;
		int numStates = productMDP.getNumStates();
		MDPRewardsSimple rewSimple = new MDPRewardsSimple(numStates);
		double currentStateDistance;
		Iterator<Entry<Integer, Double>> transitions;
		Entry<Integer, Double> transition;
		double rewardValue;
		int nextState;
		
		for (int productState = 0; productState < numStates; productState++) {
			currentStateDistance = distsToAcc.get(getAutomatonState(productState));
			int numChoices = productMDP.getNumChoices(productState);
			for (int i = 0; i < numChoices; i++) {
				transitions = productMDP.getTransitionsIterator(productState, i);
				rewardValue=0.0;
				while(transitions.hasNext()) {
					transition = transitions.next();
					nextState = transition.getKey();
					rewardValue = rewardValue + transition.getValue()*Math.max(currentStateDistance - distsToAcc.get(getAutomatonState(nextState)), 0.0);
				}
				rewSimple.setTransitionReward(productState, i, rewardValue);
			}
		}				
		return rewSimple;
	}
	

	/**
	 * Project state values from the product model back to the original model. This function
	 * assumes that the product model has at most one initial state per state in the original
	 * model. The value of this state is then projected to the corresponding state of the
	 * original model.
	 * @param sv the state values in the product model
	 * @return the corresponding state values in the original model
	 * @throws PrismException
	 */
	@Override
	public StateValues projectToOriginalModel(final StateValues sv) throws PrismException
	{
		return sv.projectToOriginalModel(this);
	}
}
