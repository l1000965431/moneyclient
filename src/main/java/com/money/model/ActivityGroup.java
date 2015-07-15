package com.money.model;

import javax.persistence.*;

/**
 * 项目组
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
 */
@Entity
@Table(name = "activitygroup")
public class ActivityGroup {
    /**
     *  项目组ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     *  项目组名称
     */
    String name;
}
