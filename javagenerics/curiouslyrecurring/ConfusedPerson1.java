package _3_curiously_recurring.non_recurring_cloneable;

import java.lang.*;

public class ConfusedPerson1 implements Cloneable<ConfusedPerson1>
{
    private String name;

    public ConfusedPerson1(final String name)
    {
        this.name = name;
    }

    public ConfusedPerson1 clone()
    {
        return new ConfusedPerson1(getName());
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return getName();
    }
}
