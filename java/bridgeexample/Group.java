package bridgeexample;

public enum Group {

    OWNER("Owner"), DEFAULT("Default");

    private String name;

    private Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
