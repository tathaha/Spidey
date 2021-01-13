package dev.mlnr.spidey.objects.akinator;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.core.entities.Guess;

import java.util.ArrayList;
import java.util.List;

public class AkinatorData
{
    private final Akiwrapper akinator;
    private final List<Long> declined;
    private Guess currentGuess;
    private boolean prompted;

    public AkinatorData(Akiwrapper akinator)
    {
        this.akinator = akinator;
        this.declined = new ArrayList<>();
    }

    public Akiwrapper getAkinator()
    {
        return this.akinator;
    }

    // declined guesses

    public void addDeclined(Guess guess)
    {
        declined.add(guess.getIdLong());
    }

    public boolean isDeclined(Guess guess)
    {
        return declined.contains(guess.getIdLong());
    }

    // current guess

    public Guess getCurrentGuess()
    {
        return this.currentGuess;
    }

    public void setCurrentGuess(Guess guess)
    {
        this.currentGuess = guess;
    }

    // prompted

    public boolean isPrompted()
    {
        return this.prompted;
    }

    public void setPrompted(boolean prompted)
    {
        this.prompted = prompted;
    }
}