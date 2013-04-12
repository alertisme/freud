package org.freud.analysed.classbytecode.method.instruction;

public final class CastOperandStack extends AbstractOperandStack
{
    private final String fromType;
    private final String toType;
    private final int category;

    public CastOperandStack(final String fromType, final String toType, final OperandStack next, final Opcode opcode)
    {
        super(next, opcode);
        this.fromType = fromType;
        this.toType = toType;
        this.category = calculateComputationalTypeCategory(toType);
    }

    @Override
    protected String getTypeForCurrentOperandStackItem()
    {
        return fromType;
    }

    @Override
    protected String toStringInternal()
    {
        return "cast to " + toType;
    }

    public String getFromType()
    {
        return fromType;
    }

    @Override
    public OperandStack dup(final OperandStack next, final Opcode opcode)
    {
        return new CastOperandStack(fromType, toType, next, opcode);
    }

    @Override
    public int getComputationalTypeCategory()
    {
        return category;
    }
}
