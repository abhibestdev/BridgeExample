package bridgeexample;

public class PlayerData {

    private Group group;

    public PlayerData() {
        group = Group.DEFAULT;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
