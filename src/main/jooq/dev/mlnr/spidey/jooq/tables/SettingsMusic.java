/*
 * This file is generated by jOOQ.
 */
package dev.mlnr.spidey.jooq.tables;


import dev.mlnr.spidey.jooq.Keys;
import dev.mlnr.spidey.jooq.Public;
import dev.mlnr.spidey.jooq.tables.records.SettingsMusicRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
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
public class SettingsMusic extends TableImpl<SettingsMusicRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.settings_music</code>
     */
    public static final SettingsMusic SETTINGS_MUSIC = new SettingsMusic();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SettingsMusicRecord> getRecordType() {
        return SettingsMusicRecord.class;
    }

    /**
     * The column <code>public.settings_music.guild_id</code>.
     */
    public final TableField<SettingsMusicRecord, Long> GUILD_ID = createField(DSL.name("guild_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.settings_music.dj_role_id</code>.
     */
    public final TableField<SettingsMusicRecord, Long> DJ_ROLE_ID = createField(DSL.name("dj_role_id"), SQLDataType.BIGINT.defaultValue(DSL.field("'0'::bigint", SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.settings_music.segment_skipping_enabled</code>.
     */
    public final TableField<SettingsMusicRecord, Boolean> SEGMENT_SKIPPING_ENABLED = createField(DSL.name("segment_skipping_enabled"), SQLDataType.BOOLEAN.defaultValue(DSL.field("false", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>public.settings_music.default_volume</code>.
     */
    public final TableField<SettingsMusicRecord, Integer> DEFAULT_VOLUME = createField(DSL.name("default_volume"), SQLDataType.INTEGER.defaultValue(DSL.field("100", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>public.settings_music.fair_queue_enabled</code>.
     */
    public final TableField<SettingsMusicRecord, Boolean> FAIR_QUEUE_ENABLED = createField(DSL.name("fair_queue_enabled"), SQLDataType.BOOLEAN.defaultValue(DSL.field("false", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>public.settings_music.fair_queue_threshold</code>.
     */
    public final TableField<SettingsMusicRecord, Integer> FAIR_QUEUE_THRESHOLD = createField(DSL.name("fair_queue_threshold"), SQLDataType.INTEGER.defaultValue(DSL.field("3", SQLDataType.INTEGER)), this, "");

    private SettingsMusic(Name alias, Table<SettingsMusicRecord> aliased) {
        this(alias, aliased, null);
    }

    private SettingsMusic(Name alias, Table<SettingsMusicRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.settings_music</code> table reference
     */
    public SettingsMusic(String alias) {
        this(DSL.name(alias), SETTINGS_MUSIC);
    }

    /**
     * Create an aliased <code>public.settings_music</code> table reference
     */
    public SettingsMusic(Name alias) {
        this(alias, SETTINGS_MUSIC);
    }

    /**
     * Create a <code>public.settings_music</code> table reference
     */
    public SettingsMusic() {
        this(DSL.name("settings_music"), null);
    }

    public <O extends Record> SettingsMusic(Table<O> child, ForeignKey<O, SettingsMusicRecord> key) {
        super(child, key, SETTINGS_MUSIC);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<SettingsMusicRecord> getPrimaryKey() {
        return Keys.SETTINGS_MUSIC_PKEY;
    }

    @Override
    public List<ForeignKey<SettingsMusicRecord, ?>> getReferences() {
        return Arrays.asList(Keys.SETTINGS_MUSIC__SETTINGS_MUSIC_GUILD_ID_FKEY);
    }

    private transient Guilds _guilds;

    /**
     * Get the implicit join path to the <code>public.guilds</code> table.
     */
    public Guilds guilds() {
        if (_guilds == null)
            _guilds = new Guilds(this, Keys.SETTINGS_MUSIC__SETTINGS_MUSIC_GUILD_ID_FKEY);

        return _guilds;
    }

    @Override
    public SettingsMusic as(String alias) {
        return new SettingsMusic(DSL.name(alias), this);
    }

    @Override
    public SettingsMusic as(Name alias) {
        return new SettingsMusic(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SettingsMusic rename(String name) {
        return new SettingsMusic(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SettingsMusic rename(Name name) {
        return new SettingsMusic(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, Long, Boolean, Integer, Boolean, Integer> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
