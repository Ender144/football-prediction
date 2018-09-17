package org.clarke.database.regularSeasonModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@SuppressWarnings({"WeakerAccess", "unused"})
@Table(schema = "regular_season", name = "Broadcast", uniqueConstraints = {@UniqueConstraint(columnNames = "broadcast_id")})
public class DbBroadcast
{
    @Transient
    public static final String ID = "broadcast_id";
    @Transient
    public static final String NETWORK = "network";
    @Transient
    public static final String SATELLITE = "satellite";

    @Id
    @GeneratedValue
    @Column(name = ID)
    private int id;

    @Column(name = NETWORK, nullable = false)
    private String network = "unknown";

    @Column(name = SATELLITE, nullable = false)
    private String satellite = "unknown";

    @OneToOne(mappedBy = "dbBroadcast", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbGame dbGame;

    public DbBroadcast() {}

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getNetwork()
    {
        return network;
    }

    public void setNetwork(String network)
    {
        if (network == null)
        {
            this.network = "unknown";
        } else
        {
            this.network = network;
        }
    }

    public String getSatellite()
    {
        return satellite;
    }

    public void setSatellite(String satellite)
    {
        if (satellite == null)
        {
            this.satellite = "unknown";
        } else
        {
            this.satellite = satellite;
        }
    }
}
