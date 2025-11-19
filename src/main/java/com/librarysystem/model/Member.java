package com.librarysystem.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a library member/user.
 */
public class Member implements Serializable, Comparable<Member> {
    private static final long serialVersionUID = 1L;

    private String memberId;
    private String name;
    private String email;
    private String phone;
    private int maxCheckouts;
    private MembershipStatus status;

    public enum MembershipStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }

    public Member(String memberId, String name, String email, String phone) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.maxCheckouts = 5;
        this.status = MembershipStatus.ACTIVE;
    }

    // Getters
    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public int getMaxCheckouts() {
        return maxCheckouts;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public boolean isActive() {
        return status == MembershipStatus.ACTIVE;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setMaxCheckouts(int maxCheckouts) {
        if (maxCheckouts < 0) {
            throw new IllegalArgumentException("Max checkouts cannot be negative");
        }
        this.maxCheckouts = maxCheckouts;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    @Override
    public int compareTo(Member other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return memberId.equals(member.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }

    @Override
    public String toString() {
        return String.format(
            "Member{memberId='%s', name='%s', email='%s', phone='%s', status=%s}",
            memberId, name, email, phone, status
        );
    }
}
