/*
 * This file is generated by jOOQ.
 */
package dev.mlnr.spidey.jooq.tables.records;


import dev.mlnr.spidey.jooq.tables.InviteFilterIgnoredUsers;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class InviteFilterIgnoredUsersRecord extends TableRecordImpl<InviteFilterIgnoredUsersRecord> implements Record2<Long, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.invite_filter_ignored_users.guild_id</code>.
     */
    public InviteFilterIgnoredUsersRecord setGuildId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.invite_filter_ignored_users.guild_id</code>.
     */
    public Long getGuildId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.invite_filter_ignored_users.user_id</code>.
     */
    public InviteFilterIgnoredUsersRecord setUserId(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.invite_filter_ignored_users.user_id</code>.
     */
    public Long getUserId() {
        return (Long) get(1);
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<Long, Long> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<Long, Long> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return InviteFilterIgnoredUsers.INVITE_FILTER_IGNORED_USERS.GUILD_ID;
    }

    @Override
    public Field<Long> field2() {
        return InviteFilterIgnoredUsers.INVITE_FILTER_IGNORED_USERS.USER_ID;
    }

    @Override
    public Long component1() {
        return getGuildId();
    }

    @Override
    public Long component2() {
        return getUserId();
    }

    @Override
    public Long value1() {
        return getGuildId();
    }

    @Override
    public Long value2() {
        return getUserId();
    }

    @Override
    public InviteFilterIgnoredUsersRecord value1(Long value) {
        setGuildId(value);
        return this;
    }

    @Override
    public InviteFilterIgnoredUsersRecord value2(Long value) {
        setUserId(value);
        return this;
    }

    @Override
    public InviteFilterIgnoredUsersRecord values(Long value1, Long value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached InviteFilterIgnoredUsersRecord
     */
    public InviteFilterIgnoredUsersRecord() {
        super(InviteFilterIgnoredUsers.INVITE_FILTER_IGNORED_USERS);
    }

    /**
     * Create a detached, initialised InviteFilterIgnoredUsersRecord
     */
    public InviteFilterIgnoredUsersRecord(Long guildId, Long userId) {
        super(InviteFilterIgnoredUsers.INVITE_FILTER_IGNORED_USERS);

        setGuildId(guildId);
        setUserId(userId);
    }
}
