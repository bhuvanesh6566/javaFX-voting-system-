package miniproject;

/**
 * Represents a candidate in the voting system.
 */
public class Candidate {
    private final String name;
    private int votes;

    public Candidate(String name) {
        this.name = name;
        this.votes = 0;
    }

    public Candidate(String name, int votes) {
        this.name = name;
        this.votes = votes;
    }

    public String getName() {
        return name;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void incrementVotes() {
        votes++;
    }

    @Override
    public String toString() {
        return name + " (" + votes + " votes)";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Candidate candidate = (Candidate) obj;
        return name != null ? name.equals(candidate.name) : candidate.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}

