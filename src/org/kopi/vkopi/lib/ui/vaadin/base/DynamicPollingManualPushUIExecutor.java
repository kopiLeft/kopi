/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.vkopi.lib.ui.vaadin.base;

import org.kopi.vkopi.lib.ui.vaadin.base.UIRunnableFuture.CompleteEvent;
import org.kopi.vkopi.lib.ui.vaadin.base.UIRunnableFuture.CompleteListener;

import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.UI;

/**
 * A {@link UIExecutor} that dynamically updates the polling interval on the UI
 * to perform faster polling when a background task is e executing in an attempt
 * to improve responsiveness when waiting for a result while limiting
 * unnecessary polling when there are no expected server side changes. If the UI
 * is configured for push rather than polling, the polling interval will not be
 * changed but the {@link UI#push()} method will be called when a task
 * completes.
 *
 * @deprecated Use the {@link StrategyUIExecutor} instead
 */
@Deprecated
public class DynamicPollingManualPushUIExecutor extends CurrentUIExecutor {

  /**
   * The default poll interval when background work is executing in a runnable.
   */
  public static final int DEFAULT_WORK_POLL_INTERVAL = 800;

  /**
   * The default poll interval when no background work is executing.
   */
  public static final int DEFAULT_NORMAL_POLL_INTERVAL = 15000;

  private int workPollInterval = DEFAULT_WORK_POLL_INTERVAL;
  private int normalPollInterval = DEFAULT_NORMAL_POLL_INTERVAL;

  /**
   * A count of the number of pending background tasks.
   */
  private int pendingTasks;

  /*
   * (non-Javadoc)
   *
   * @see
   * org.prss.contentdepot.vaadin.uitask.CurrentUIExecutor#prepareForExecution
   * (com.vaadin.ui.UI, org.prss.contentdepot.vaadin.uitask.UIRunnableFuture)
   */
  @Override
  protected void prepareForExecution(UI ui, UIRunnableFuture runnableFuture) {
    super.prepareForExecution(ui, runnableFuture);

    // We handle the start now even though the runnable may be getting queued in
    // the executor. This is needed because we want any poll time changes to the
    // UI to happen in the first client response otherwise the client wouldn't
    // know that it should use the fast poll until the first slow poll got the
    // change to the fast poll.
    taskStart(ui);

    // Add a complete listener so we can slow the polling down if all background
    // work is done.
    runnableFuture.addCompleteListener(createCompleteListener());
  }

  /**
   * Constructs the complete listener to be notified when a submitted task
   * completes execution.
   *
   * @return the complete listener
   */
  private CompleteListener createCompleteListener() {
    return new CompleteListener() {

      @Override
      public void uiRunnableComplete(CompleteEvent evt) {
        UIRunnableFuture taskRunnable = (UIRunnableFuture) evt.getSource();
        UI ui = taskRunnable.getUi();

        taskFinish(ui);
      }
    };
  }

  /**
   * Called by the complete listener to adjust the polling interval or perform a
   * manual push.
   *
   * @param ui
   *          the UI to update or push
   */
  private synchronized void taskFinish(UI ui) {
    pendingTasks = Math.max(pendingTasks - 1, 0);

    if (ui.getPushConfiguration().getPushMode() == PushMode.DISABLED
      && pendingTasks == 0 && ui.getPollInterval() != normalPollInterval) {

      // Decrease the poll interval because there are no more running tasks.
      ui.setPollInterval(normalPollInterval);
    } else if (ui.getPushConfiguration().getPushMode() == PushMode.MANUAL) {

      // Do a manual push because some background task just finished and
      // presumably made changes to the UI.
      ui.push();
    }
  }

  /**
   * Called when a task is submitted for execution to adjust the polling
   * interval.
   *
   * @param ui
   *          the UI to update
   */
  private synchronized void taskStart(UI ui) {

    if (ui.getPushConfiguration().getPushMode() == PushMode.DISABLED
      && ui.getPollInterval() != workPollInterval) {

      // Increase the poll interval because there are running tasks.
      ui.setPollInterval(workPollInterval);
    }

    pendingTasks++;
  }

  /**
   * Sets the poll interval when work is pending and therefore the client is
   * expecting a server s generated change. This interval is normally small
   * (e.g. 800 milliseconds).
   *
   * @param workPollInterval
   *          the interval in milliseconds
   */
  public void setWorkPollInterval(int workPollInterval) {
    this.workPollInterval = workPollInterval;
  }

  /**
   * Sets the poll interval when work is not pending and therefore the client is
   * not expecting a server generated change. This interval is normally large
   * (e.g. 15000 milliseconds) to limit wasted network round trips.
   *
   * @param normalPollInterval
   *          the interval in milliseconds
   */
  public void setNormalPollInterval(int normalPollInterval) {
    this.normalPollInterval = normalPollInterval;
  }
}
