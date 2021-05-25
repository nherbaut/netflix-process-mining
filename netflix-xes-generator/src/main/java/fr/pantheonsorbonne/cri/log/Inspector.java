package fr.pantheonsorbonne.cri.log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pantheonsorbonne.ufr27.miage.model.xes.EventType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.Log;

public final class Inspector implements Runnable {
	private final Log log;
	private boolean stopped = false;
	private ThreadPoolExecutor es;

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	private final static Logger LOGGER = LoggerFactory.getLogger(Inspector.class);

	public Inspector(Log log, ExecutorService es) {
		this.log = log;
		if (es instanceof ThreadPoolExecutor) {
			this.es = (ThreadPoolExecutor) es;
		}
	}

	@Override
	public void run() {
		while (!stopped) {
			Integer count = 0;
			long pending = -1;
			if (es != null) {
				pending = es.getTaskCount() - es.getActiveCount() - es.getCompletedTaskCount();
			}
			LOGGER.info("pending tasks = " + pending + " ntraces = " + log.getTraces().stream().count() + " nevent = "
					+ log.getTraces().stream().filter(t -> t != null && t.getEvents() != null).map(t -> t.getEvents())
							.reduce(count, new BiFunction<Integer, List<EventType>, Integer>() {

								@Override
								public Integer apply(Integer t, List<EventType> u) {
									return t + u.size();
								}

							}, new BinaryOperator<Integer>() {

								@Override
								public Integer apply(Integer t, Integer u) {
									return t + u;
								}
							}));
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}