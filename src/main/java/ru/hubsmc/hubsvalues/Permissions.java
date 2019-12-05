package ru.hubsmc.hubsvalues;

public class Permissions {

    public enum Perm {
        HELP("hubsval.help"),
        RELOAD("hubsval.reload"),
        CHECK("hubsval.check"),
        CHANGE("hubsval.change"),
        PAY("hubsval.pay"),
        PAY_ALL("hubsval.pay.all"),
        TOP("hubsval.top");

        private final String perm;

        Perm(String perm) {
            this.perm = perm;
        }

        public String getPerm() {
            return perm;
        }
    }

}
