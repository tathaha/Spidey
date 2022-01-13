/*
 * This file is generated by jOOQ.
 */
package dev.mlnr.spidey.jooq.tables;


import dev.mlnr.spidey.jooq.Keys;
import dev.mlnr.spidey.jooq.Public;
import dev.mlnr.spidey.jooq.tables.records.SettingsMiscRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SettingsMisc extends TableImpl<SettingsMiscRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.settings_misc</code>
     */
    public static final SettingsMisc SETTINGS_MISC = new SettingsMisc();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SettingsMiscRecord> getRecordType() {
        return SettingsMiscRecord.class;
    }

    /**
     * The column <code>public.settings_misc.guild_id</code>.
     */
    public final TableField<SettingsMiscRecord, Long> GUILD_ID = createField(DSL.name("guild_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.settings_misc.log_channel_id</code>.
     */
    public final TableField<SettingsMiscRecord, Long> LOG_CHANNEL_ID = createField(DSL.name("log_channel_id"), SQLDataType.BIGINT.defaultValue(DSL.field("'0'::bigint", SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.settings_misc.join_role_id</code>.
     */
    public final TableField<SettingsMiscRecord, Long> JOIN_ROLE_ID = createField(DSL.name("join_role_id"), SQLDataType.BIGINT.defaultValue(DSL.field("'0'::bigint", SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.settings_misc.sniping_enabled</code>.
     */
    public final TableField<SettingsMiscRecord, Boolean> SNIPING_ENABLED = createField(DSL.name("sniping_enabled"), SQLDataType.BOOLEAN.defaultValue(DSL.field("true", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>public.settings_misc.language</code>.
     */
    public final TableField<SettingsMiscRecord, String> LANGUAGE = createField(DSL.name("language"), SQLDataType.VARCHAR(2).nullable(false).defaultValue(DSL.field("'en'::character varying", SQLDataType.VARCHAR)), this, "");

    private SettingsMisc(Name alias, Table<SettingsMiscRecord> aliased) {
        this(alias, aliased, null);
    }

    private SettingsMisc(Name alias, Table<SettingsMiscRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.settings_misc</code> table reference
     */
    public SettingsMisc(String alias) {
        this(DSL.name(alias), SETTINGS_MISC);
    }

    /**
     * Create an aliased <code>public.settings_misc</code> table reference
     */
    public SettingsMisc(Name alias) {
        this(alias, SETTINGS_MISC);
    }

    /**
     * Create a <code>public.settings_misc</code> table reference
     */
    public SettingsMisc() {
        this(DSL.name("settings_misc"), null);
    }

    public <O extends Record> SettingsMisc(Table<O> child, ForeignKey<O, SettingsMiscRecord> key) {
        super(child, key, SETTINGS_MISC);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<SettingsMiscRecord> getPrimaryKey() {
        return Keys.SETTINGS_MISC_PKEY;
    }

    @Override
    public List<ForeignKey<SettingsMiscRecord, ?>> getReferences() {
        return Arrays.asList(Keys.SETTINGS_MISC__SETTINGS_MISC_GUILD_ID_FKEY);
    }

    private transient Guilds _guilds;

    /**
     * Get the implicit join path to the <code>public.guilds</code> table.
     */
    public Guilds guilds() {
        if (_guilds == null)
            _guilds = new Guilds(this, Keys.SETTINGS_MISC__SETTINGS_MISC_GUILD_ID_FKEY);

        return _guilds;
    }

    @Override
    public SettingsMisc as(String alias) {
        return new SettingsMisc(DSL.name(alias), this);
    }

    @Override
    public SettingsMisc as(Name alias) {
        return new SettingsMisc(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SettingsMisc rename(String name) {
        return new SettingsMisc(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SettingsMisc rename(Name name) {
        return new SettingsMisc(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, Long, Long, Boolean, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
