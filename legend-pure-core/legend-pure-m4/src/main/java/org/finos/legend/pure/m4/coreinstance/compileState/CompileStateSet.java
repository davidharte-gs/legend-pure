// Copyright 2020 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.pure.m4.coreinstance.compileState;

import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.impl.set.immutable.AbstractImmutableSet;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public class CompileStateSet extends AbstractImmutableSet<CompileState>
{
    private static final CompileStateSet[] CACHED_SETS = {new CompileStateSet(0), new CompileStateSet(1), new CompileStateSet(2), new CompileStateSet(3)};
    private static final CompileState[] ALL_STATES = CompileState.values();
    private static final int ALL_BIT_FLAGS = (1 << ALL_STATES.length) - 1;
    private static final int ORDINARY_BIT_FLAGS = toBitSet(CompileState.PROCESSED, CompileState.VALIDATED);

    public static final CompileStateSet PROCESSED_VALIDATED = with(CompileState.PROCESSED, CompileState.VALIDATED);

    private final int bits;

    private CompileStateSet(int bits)
    {
        this.bits = bits;
    }

    public int toBitSet()
    {
        return this.bits;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (object instanceof CompileStateSet)
        {
            return this.bits == ((CompileStateSet) object).bits;
        }

        if (!(object instanceof Set))
        {
            return false;
        }

        Set<?> other = (Set<?>) object;
        return this.size() == other.size() && this.containsAll(other);
    }

    @Override
    public int hashCode()
    {
        int hashCode = 0;
        if (this.bits != 0)
        {
            for (CompileState state : this)
            {
                hashCode += state.hashCode();
            }
        }
        return hashCode;
    }

    @Override
    public boolean contains(Object o)
    {
        return (o instanceof CompileState) && contains((CompileState) o);
    }

    public boolean contains(CompileState state)
    {
        return bitSetHasCompileState(this.bits, state);
    }

    @Override
    public int size()
    {
        return Integer.bitCount(this.bits);
    }

    @Override
    public boolean isEmpty()
    {
        return this.bits == 0;
    }

    @Override
    public CompileState getFirst()
    {
        return getFirst(this.bits);
    }

    @Override
    public CompileState getLast()
    {
        return getLast(this.bits);
    }

    @Override
    public void forEach(Consumer<? super CompileState> consumer)
    {
        forEach(this.bits, consumer);
    }

    @Override
    public void each(Procedure<? super CompileState> procedure)
    {
        forEach(this.bits, procedure);
    }

    @Override
    public Iterator<CompileState> iterator()
    {
        return new CompileStateSetIterator(this.bits);
    }

    @Override
    public Spliterator<CompileState> spliterator()
    {
        return Spliterators.spliterator(this, Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL);
    }

    @Override
    public CompileStateSet clone()
    {
        return this;
    }

    @Override
    public CompileStateSet newWith(CompileState state)
    {
        return contains(state) ? this : fromBitSet(addCompileStateToBitSet(this.bits, state));
    }

    @Override
    public CompileStateSet newWithout(CompileState state)
    {
        return contains(state) ? fromBitSet(removeCompileStatesFromBitSet(this.bits, state)) : this;
    }

    @Override
    public CompileStateSet newWithAll(Iterable<? extends CompileState> states)
    {
        return fromBitSet(addCompileStatesToBitSet(this.bits, states));
    }

    @Override
    public CompileStateSet newWithoutAll(Iterable<? extends CompileState> states)
    {
        return fromBitSet(removeCompileStatesFromBitSet(this.bits, states));
    }

    private static class CompileStateSetIterator implements Iterator<CompileState>
    {
        private int unseen;

        private CompileStateSetIterator(int bits)
        {
            this.unseen = bits;
        }

        @Override
        public boolean hasNext()
        {
            return this.unseen != 0;
        }

        @Override
        public CompileState next()
        {
            if (this.unseen == 0)
            {
                throw new NoSuchElementException();
            }
            int next = this.unseen & -this.unseen;
            this.unseen -= next;
            return getFirst(next);
        }

        @Override
        public void forEachRemaining(Consumer<? super CompileState> action)
        {
            while (this.unseen != 0)
            {
                this.unseen = consumeNext(this.unseen, action);
            }
        }
    }

    public static int getBitFlag(CompileState state)
    {
        return 1 << state.ordinal();
    }

    public static boolean bitSetHasCompileState(int bits, CompileState state)
    {
        return (bits & getBitFlag(state)) != 0;
    }

    public static int addCompileStateToBitSet(int bits, CompileState state)
    {
        return bits | getBitFlag(state);
    }

    public static int addCompileStatesToBitSet(int bits, CompileState... states)
    {
        int result = bits;
        for (int i = 0; i < states.length; i++)
        {
            result |= getBitFlag(states[i]);
        }
        return result;
    }

    public static int addCompileStatesToBitSet(int bits, CompileStateSet states)
    {
        return bits | states.bits;
    }

    public static int addCompileStatesToBitSet(int bits, Iterable<? extends CompileState> states)
    {
        if (states instanceof CompileStateSet)
        {
            return addCompileStatesToBitSet(bits, (CompileStateSet) states);
        }

        int result = bits;
        for (CompileState state : states)
        {
            result |= getBitFlag(state);
        }
        return result;
    }

    public static int removeCompileStateFromBitSet(int bits, CompileState state)
    {
        return bits & ~getBitFlag(state);
    }

    public static int removeCompileStatesFromBitSet(int bits, CompileState... states)
    {
        int result = bits;
        for (int i = 0; i < states.length; i++)
        {
            result &= ~getBitFlag(states[i]);
        }
        return result;
    }

    public static int removeCompileStatesFromBitSet(int bits, CompileStateSet states)
    {
        return bits & ~states.bits;
    }

    public static int removeCompileStatesFromBitSet(int bits, Iterable<? extends CompileState> states)
    {
        if (states instanceof CompileStateSet)
        {
            return removeCompileStatesFromBitSet(bits, (CompileStateSet) states);
        }

        int result = bits;
        for (CompileState state : states)
        {
            result &= ~getBitFlag(state);
        }
        return result;
    }

    public static int countCompileStatesInBitSet(int bits)
    {
        return Integer.bitCount(normalizeCompileStateBitSet(bits));
    }

    public static int toBitSet(CompileState state)
    {
        return getBitFlag(state);
    }

    public static int toBitSet(CompileState state1, CompileState state2)
    {
        return getBitFlag(state1) | getBitFlag(state2);
    }

    public static int toBitSet(CompileState... states)
    {
        return addCompileStatesToBitSet(0, states);
    }

    public static int toBitSet(CompileStateSet states)
    {
        return states.bits;
    }

    public static int toBitSet(Iterable<? extends CompileState> states)
    {
        return (states instanceof CompileStateSet) ? toBitSet((CompileStateSet) states) : addCompileStatesToBitSet(0, states);
    }

    /**
     * Remove extra compile states from a bit set.
     *
     * @param bits compile state bit set
     * @return compile state bit set with extra states removed
     */
    public static int removeExtraCompileStates(int bits)
    {
        return bits & ORDINARY_BIT_FLAGS;
    }

    /**
     * Normalize a compile state bit set by zeroing out bits
     * that don't correspond to compile states.
     *
     * @param bits compile state bit set
     * @return normalized compile state bit set
     */
    public static int normalizeCompileStateBitSet(int bits)
    {
        return bits & ALL_BIT_FLAGS;
    }

    public static CompileStateSet with()
    {
        return CACHED_SETS[0];
    }

    public static CompileStateSet with(CompileState state)
    {
        return fromNormalizedBitSet(toBitSet(state));
    }

    public static CompileStateSet with(CompileState state1, CompileState state2)
    {
        return fromNormalizedBitSet(toBitSet(state1, state2));
    }

    public static CompileStateSet with(CompileState... states)
    {
        return fromNormalizedBitSet(toBitSet(states));
    }

    public static CompileStateSet withAll(Iterable<? extends CompileState> states)
    {
        return fromNormalizedBitSet(toBitSet(states));
    }

    public static CompileStateSet fromBitSet(int bits)
    {
        return fromNormalizedBitSet(normalizeCompileStateBitSet(bits));
    }

    private static CompileStateSet fromNormalizedBitSet(int bits)
    {
        return (bits < CACHED_SETS.length) ? CACHED_SETS[bits] : new CompileStateSet(bits);
    }

    private static CompileState getFirst(int bits)
    {
        return (bits == 0) ? null : ALL_STATES[Integer.numberOfTrailingZeros(bits)];
    }

    private static CompileState getLast(int bits)
    {
        return (bits == 0) ? null : ALL_STATES[31 - Integer.numberOfLeadingZeros(bits)];
    }

    private static void forEach(int bits, Consumer<? super CompileState> consumer)
    {
        while (bits != 0)
        {
            bits = consumeNext(bits, consumer);
        }
    }

    private static int consumeNext(int bits, Consumer<? super CompileState> consumer)
    {
        int next = bits & -bits;
        CompileState state = getFirst(next);
        consumer.accept(state);
        return bits - next;
    }
}
