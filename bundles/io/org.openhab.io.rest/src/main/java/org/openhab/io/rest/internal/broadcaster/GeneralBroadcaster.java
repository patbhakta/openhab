/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.rest.internal.broadcaster;

import java.util.Collection;
import java.util.Collections;
import java.util.WeakHashMap;

import org.atmosphere.cpr.BroadcasterLifeCyclePolicyListener;
import org.atmosphere.jersey.JerseyBroadcaster;
import org.openhab.io.rest.internal.listeners.ResourceStateChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Oliver Mazur
 *
 */
public class GeneralBroadcaster extends JerseyBroadcaster {
	
	private static final Logger logger = LoggerFactory.getLogger(GeneralBroadcaster.class);
	protected Collection<ResourceStateChangeListener> listeners = Collections.newSetFromMap(new WeakHashMap<ResourceStateChangeListener, Boolean>());
	
	public GeneralBroadcaster(String id, org.atmosphere.cpr.AtmosphereConfig config) {
		super(id, config);
		this.addBroadcasterLifeCyclePolicyListener(new BroadcasterLifeCyclePolicyListener() {
			
			@Override
			public void onIdle() {
				logger.debug("broadcaster '{}' is idle", this.toString());
			}
			
			@Override
			public void onEmpty() {
				logger.debug("broadcaster '{}' is empty", this.toString());
			/*	for (ResourceStateChangeListener l : listeners){
					l.unregisterItems();
					listeners.remove(l);

				} */
				
			}
			
			@Override
			public void onDestroy() {
				logger.debug("broadcaster '{}' destroyed", this.toString());
				for (ResourceStateChangeListener l : listeners){
					l.unregisterItems();
					listeners.remove(l);
				}
			}
		});
	}
	
	public void addStateChangeListener(final ResourceStateChangeListener listener){
		synchronized (listeners) {
			if(listeners.isEmpty()){
				listener.setBroadcaster(this);
				listener.registerItems();
				listeners.add(listener);	
			}
		}

	}


}
