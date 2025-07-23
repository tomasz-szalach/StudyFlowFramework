package com.example.studyflowframework.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_roles")
public class UserRole {

    @Id
    @Column(name = "role_id")
    private Short roleId;

    @Column(name = "role_code", unique = true, nullable = false, length = 20)
    private String roleCode;          //  USER / ADMIN …

    @Column(name = "role_name", length = 64)
    private String roleName;          //  opis (opcjonalnie PL)

    /* ==== get / set ==== */
    public Short  getRoleId()   { return roleId; }
    public String getRoleCode() { return roleCode; }   // ← **BRAKOWAŁO**
    public String getRoleName() { return roleName; }

    public void setRoleId(Short roleId)     { this.roleId = roleId; }
    public void setRoleCode(String code)    { this.roleCode = code; }
    public void setRoleName(String name)    { this.roleName = name; }
}
