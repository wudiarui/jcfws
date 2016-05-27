package org.jerry.frameworks.base.plugin.entity;

/**
 * 记录状态的接口
 *
 * <p>Date : 16/5/23</p>
 * <p>Time : 上午8:48</p>
 *
 * @author jerry
 */
public interface Stateable<T extends Enum<? extends Stateable.Status>> {

    void setStatus(T status);

    Status getStatus();

    /**
     * 标识接口，所有状态实现，需要实现该状态接口
     */
    interface Status {
    }

    enum AuditStatus implements Status {
        waiting("等待审核"), fail("审核失败"), success("审核成功");

        private final String info;

        AuditStatus(String info) {
            this.info = info;
        }

        public String getInfo() {
            return info;
        }
    }

    enum ShowStatus implements Status {
        hide("不显示"), show("显示");
        private final String info;

        ShowStatus(String info) {
            this.info = info;
        }

        public String getInfo() {
            return info;
        }
    }
}
