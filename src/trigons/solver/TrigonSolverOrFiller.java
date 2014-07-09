package trigons.solver;

import java.util.Collection;

import trigons.solver.TrigonValueSet.TrigonValuePermutation;

public interface TrigonSolverOrFiller {

	Collection<TrigonValuePermutation> getPerms(int triX, int triY);

}
