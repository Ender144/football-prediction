package org.clarke.predictionModel;

public enum Outcome
{
    WIN("W"),
    LOSE("L"),
    UNPLAYED("Unplayed");

    private String printableOutcome;

    Outcome(String printableOutcome)
    {
        this.printableOutcome = printableOutcome;
    }

    @Override
    public String toString()
    {
        return printableOutcome;
    }
}
