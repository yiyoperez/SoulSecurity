package com.strixmc.souls.utilities;

import java.util.ArrayList;

public class MembersManager {

    private ArrayList<Member> membersList = new ArrayList<Member>();

    public ArrayList<Member> getMembersList() {
        return membersList;
    }

    public Member getMember(String player) {
        for (Member member : membersList) {
            if (member.getPlayer().getName().equalsIgnoreCase(player)) {
                return member;
            }
        }
        return null;
    }

    public boolean containsMember(String player) {
        for (Member member : membersList) {
            if (member.getPlayer().getName().equalsIgnoreCase(player)) {
                return true;
            }
        }
        return false;
    }

    public boolean addMember(Member member) {
        if (!containsMember(member.getPlayer().getName())) {
            membersList.add(member);
            return true;
        }
        return false;
    }

    public boolean removeMember(String player) {
        if (containsMember(player)) {
            for (int i = 0; i < membersList.size(); i++) {
                if (membersList.get(i).getPlayer().getName().equalsIgnoreCase(player)) {
                    membersList.remove(membersList.get(i));
                }
            }
            return true;
        }
        return false;
    }

}
