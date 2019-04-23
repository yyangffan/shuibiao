package com.jiayuan.shuibiao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity(
        nameInDb = "PIPE_DATA",
        createInDb = false,
        // 在这里定义多个列的索引
        indexes = {
                @Index(value = "lat"),
                @Index(value = "lon")
        })
public class PipeData {

    @Id(autoincrement = true)
    private Long id;

    private String objectId;

    private String pipeMaterial;

    private String localityRoad;

    private String installunit;

    private String projectName;

    private String pipeAddress;

    private String embedmode;

    private String adminName;

    private String pipeType;

    //纬度
    private double lat;
    //经度
    private double lon;

    private String coordinates;

    public PipeData(String objectId, String pipeMaterial,
                    String localityRoad, String installunit, String projectName,
                    String pipeAddress, String embedmode, String adminName, String pipeType,
                    double lat, double lon, String coordinates) {
        this.objectId = objectId;
        this.pipeMaterial = pipeMaterial;
        this.localityRoad = localityRoad;
        this.installunit = installunit;
        this.projectName = projectName;
        this.pipeAddress = pipeAddress;
        this.embedmode = embedmode;
        this.adminName = adminName;
        this.pipeType = pipeType;
        this.lat = lat;
        this.lon = lon;
        this.coordinates = coordinates;
    }


@Generated(hash = 635945959)
public PipeData(Long id, String objectId, String pipeMaterial,
                String localityRoad, String installunit, String projectName,
                String pipeAddress, String embedmode, String adminName, String pipeType,
                double lat, double lon, String coordinates) {
    this.id = id;
    this.objectId = objectId;
    this.pipeMaterial = pipeMaterial;
    this.localityRoad = localityRoad;
    this.installunit = installunit;
    this.projectName = projectName;
    this.pipeAddress = pipeAddress;
    this.embedmode = embedmode;
    this.adminName = adminName;
    this.pipeType = pipeType;
    this.lat = lat;
    this.lon = lon;
    this.coordinates = coordinates;
}

@Generated(hash = 1495094007)
public PipeData() {
}

public Long getId() {
    return this.id;
}

public void setId(Long id) {
    this.id = id;
}

public String getObjectId() {
    return this.objectId;
}

public void setObjectId(String objectId) {
    this.objectId = objectId;
}

public String getPipeMaterial() {
    return this.pipeMaterial;
}

public void setPipeMaterial(String pipeMaterial) {
    this.pipeMaterial = pipeMaterial;
}

public String getLocalityRoad() {
    return this.localityRoad;
}

public void setLocalityRoad(String localityRoad) {
    this.localityRoad = localityRoad;
}

public String getInstallunit() {
    return this.installunit;
}

public void setInstallunit(String installunit) {
    this.installunit = installunit;
}

public String getProjectName() {
    return this.projectName;
}

public void setProjectName(String projectName) {
    this.projectName = projectName;
}

public String getPipeAddress() {
    return this.pipeAddress;
}

public void setPipeAddress(String pipeAddress) {
    this.pipeAddress = pipeAddress;
}

public String getEmbedmode() {
    return this.embedmode;
}

public void setEmbedmode(String embedmode) {
    this.embedmode = embedmode;
}

public String getAdminName() {
    return this.adminName;
}

public void setAdminName(String adminName) {
    this.adminName = adminName;
}

public String getPipeType() {
    return this.pipeType;
}

public void setPipeType(String pipeType) {
    this.pipeType = pipeType;
}

public double getLat() {
    return this.lat;
}

public void setLat(double lat) {
    this.lat = lat;
}

public double getLon() {
    return this.lon;
}

public void setLon(double lon) {
    this.lon = lon;
}

public String getCoordinates() {
    return this.coordinates;
}

public void setCoordinates(String coordinates) {
    this.coordinates = coordinates;
}


}
