package org.jerry.frameworks.base.plugin.entity;

import java.io.Serializable;

/**
 * <p>Date : 16/5/26</p>
 * <p>Time : 下午2:47</p>
 *
 * @author jerry
 */
public class ZTree<ID extends Serializable> implements Serializable {

    private ID id;
    private ID pid;
    private String name;
    private String iconSkin;
    private boolean open;
    private boolean root;
    private boolean isParent;
    private boolean nocheck = false;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public ID getPid() {
        return pid;
    }

    public void setPid(ID pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconSkin() {
        return iconSkin;
    }

    public void setIconSkin(String iconSkin) {
        this.iconSkin = iconSkin;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

    public boolean isNocheck() {
        return nocheck;
    }

    public void setNocheck(boolean nocheck) {
        this.nocheck = nocheck;
    }
}
