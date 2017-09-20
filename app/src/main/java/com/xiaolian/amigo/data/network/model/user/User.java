package com.xiaolian.amigo.data.network.model.user;

import com.xiaolian.amigo.data.network.model.dto.response.EntireUserDTO;

import lombok.Data;

/**
 * <p>
 * Created by zcd on 9/19/17.
 */
@Data
public class User {
    private String residenceName;
    private Long residenceId;
    private String floor;
    private Long floorId;
    private Long id;
    private String mobile;
    private String nickName;
    private String pictureUrl;
    private String room;
    private Long roomId;
    private Long schoolId;
    private String schoolName;
    private Integer sex;
    private Integer type;

    public User() {
    }

    public User(EntireUserDTO entireUserDTO) {
        this.residenceName = entireUserDTO.getResidenceName();
        this.residenceId = entireUserDTO.getResidenceId();
        this.floor = entireUserDTO.getFloor();
        this.floorId = entireUserDTO.getFloorId();
        this.id = entireUserDTO.getId();
        this.mobile = entireUserDTO.getMobile();
        this.nickName = entireUserDTO.getNickName();
        this.pictureUrl = entireUserDTO.getPictureUrl();
        this.room = entireUserDTO.getRoom();
        this.roomId = entireUserDTO.getRoomId();
        this.schoolId = entireUserDTO.getSchoolId();
        this.schoolName = entireUserDTO.getSchoolName();
        this.sex = entireUserDTO.getSex();
        this.type = entireUserDTO.getType();
    }
}
