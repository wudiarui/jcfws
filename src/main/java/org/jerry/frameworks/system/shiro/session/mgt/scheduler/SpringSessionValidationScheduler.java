package org.jerry.frameworks.system.shiro.session.mgt.scheduler;

import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionValidationScheduler;
import org.apache.shiro.session.mgt.ValidatingSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.concurrent.TimeUnit;

/**
 * Spring的会话调度
 * 使用spring的任务调度器完成 session验证
 * 功能直接复制了{@link org.apache.shiro.session.mgt.quartz.QuartzSessionValidationScheduler}
 * <p>Date : 16/5/25</p>
 * <p>Time : 上午9:27</p>
 *
 * @author jerry
 */
public class SpringSessionValidationScheduler implements SessionValidationScheduler {

    /**
     * The default interval at which sessions will be validated (1 hour);
     */
    public static final long DEFAULT_SESSION_VALIDATION_INTERVAL = DefaultSessionManager.DEFAULT_SESSION_VALIDATION_INTERVAL;

    private static final Logger logger = LoggerFactory.getLogger(SpringSessionValidationScheduler.class);

    /**
     * spring任务调度器
     */
    private TaskScheduler scheduler;

    private volatile boolean enabled = false;

    /**
     * The session manager used to validate sessions.
     */
    private ValidatingSessionManager sessionManager;

    /**
     * The session validation interval in milliseconds.
     */
    private long sessionValidationInterval = DEFAULT_SESSION_VALIDATION_INTERVAL;

    /*--------------------------------------------
    |         C O N S T R U C T O R S           |
    ============================================*/

    /**
     * Default constructor.
     */
    public SpringSessionValidationScheduler() {
    }
    /**
     * Constructor that specifies the session manager that should be used for validating sessions.
     *
     * @param sessionManager the <tt>SessionManager</tt> that should be used to validate sessions.
     */
    public SpringSessionValidationScheduler(ValidatingSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /*--------------------------------------------
    |  A C C E S S O R S / M O D I F I E R S    |
    ============================================*/

    public TaskScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(final TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }
    public void setSessionManager(ValidatingSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Specifies how frequently (in milliseconds) this Scheduler will call the
     * {@link org.apache.shiro.session.mgt.ValidatingSessionManager#validateSessions() ValidatingSessionManager#validateSessions()} method.
     *
     * <p>Unless this method is called, the default value is {@link #DEFAULT_SESSION_VALIDATION_INTERVAL}.
     *
     * @param sessionValidationInterval
     */
    public void setSessionValidationInterval(long sessionValidationInterval) {
        this.sessionValidationInterval = sessionValidationInterval;
    }

    @Override
    public void disableSessionValidation() {
        if (logger.isDebugEnabled()) {
            logger.debug("Stopping Spring Scheduler session validation job...");
        }

        this.enabled = false;
    }

    @Override
    public void enableSessionValidation() {
        this.enabled = true;

        if (logger.isDebugEnabled()) {
            logger.debug("Scheduling session validation job using Spring Scheduler with " +
                    "session validation interval of [" + sessionValidationInterval + "]ms...");
        }

        try {
            // 定义周期触发器
            PeriodicTrigger trigger = new PeriodicTrigger(sessionValidationInterval, TimeUnit.MILLISECONDS);
            trigger.setInitialDelay(sessionValidationInterval);

            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    if (enabled) {
                        sessionManager.validateSessions();
                    }
                }
            }, trigger);

            this.enabled = true;

            if (logger.isDebugEnabled()) {
                logger.debug("Session validation job successfully scheduled with Spring Scheduler.");
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error starting the Spring Scheduler session validation job.  Session validation may not occur.", e);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
