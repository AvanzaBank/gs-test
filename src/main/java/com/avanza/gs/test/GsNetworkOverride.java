/*
 * Copyright 2017 Avanza Bank AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.avanza.gs.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GsNetworkOverride {

	private static final Logger LOG = Logger.getLogger(GsNetworkOverride.class.getName());

	/**
	 * This is the address that is used by the gigaspaces LUS lookup discovery
	 * client in {@link com.sun.jini.reggie.GigaRegistrar},
	 * defined in {@link net.jini.discovery.Constants#getAnnouncementAddress}
	 * for {@code "com.gs.multicast.announcement"}
	 */
	private static final String MULTICAST_ADDRESS = "224.0.1.188";

	private static final int MULTICAST_PORT = 4174;

	/**
	 * Overrides values resolved by GigaSpaces
	 * {@link com.gigaspaces.lrmi.nio.info.NIOInfoHelper#getLocalHostAddress} and
	 * {@link com.gigaspaces.lrmi.nio.info.NIOInfoHelper#getLocalHostName} via
	 * {@link org.jini.rio.boot.BootUtil#getHost}
	 */
	private static final String RMI_SERVER_HOSTNAME = "java.rmi.server.hostname";

	// For this to take any effect, it needs to be executed before anything
	// about GS is initialized.
	static void setSystemProperties() {
		if (System.getProperty(RMI_SERVER_HOSTNAME) != null) {
			return;
		}
		Optional<String> address = findAddressThatCanUseMulticast();
		if (!address.isPresent()) {
			LOG.log(Level.WARNING, "Could not reliably determine which network "
					+ "interface that supports multicast to " + MULTICAST_ADDRESS + " . "
					+ "You might experience problems running GigaSpaces integration tests. "
					+ "To resolve the issue, you might need to find which network "
					+ "interface to use for " + MULTICAST_ADDRESS + " by checking "
					+ "the matching line in the output from \"netstat -rn\" and "
					+ "then assigning this interface to -D" + RMI_SERVER_HOSTNAME + " ."
					+ "Flushing the routing table or restarting the computer might "
					+ "also work."
			);
		}

		final String addr = address.orElse("localhost");
		LOG.log(Level.INFO, "Setting " + RMI_SERVER_HOSTNAME + "=" + addr);
		System.setProperty(RMI_SERVER_HOSTNAME, addr);
	}

	private static Optional<String> findAddressThatCanUseMulticast() {
		try {
			for (NetworkInterface nic : Collections.list(NetworkInterface.getNetworkInterfaces())) {
				LOG.log(Level.FINER, "Testing multicast on " + nic.getName());
				if (supportsMulticast(nic)) {
					return getAddress(nic);
				}
			}
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Could not enumerate network interfaces", e);
		}
		return Optional.empty();
	}

	private static Optional<String> getAddress(NetworkInterface nic) {
		String defaultAddress = null;
		for (InetAddress addr : Collections.list(nic.getInetAddresses())) {
			String s = addr.getHostAddress();
			if (looksLikeIpv4Address(s)) {
				return Optional.of(s); // Prefer ipv4 addresses on "localhost" interface
			}
			defaultAddress = s;
		}
		return Optional.ofNullable(defaultAddress);
	}

	private static boolean looksLikeIpv4Address(String s) {
		return s.contains(".");
	}

	private static boolean supportsMulticast(NetworkInterface nic) {
		try {
			if (!nic.isUp()) {
				return false;
			}
			if (!nic.supportsMulticast()) {
				return false;
			}
			testMulticastOnInterface(nic);
			return true;
		} catch (Exception e) {
			LOG.log(Level.FINER, "Unable to determine if NIC=[" + nic + "] supports multicast, message=["
								 + e != null ? e.getMessage() : null + "]", e);
			return false;
		}
	}

	private static void testMulticastOnInterface(NetworkInterface nic) throws IOException {
		final int port = MULTICAST_PORT;
		final int timeout = (int) Duration.ofMillis(10).toMillis();
		final InetAddress multicastAddress = InetAddress.getByName(MULTICAST_ADDRESS);

		try (MulticastSocket receiveSocket = new MulticastSocket(port);
				MulticastSocket sendSocket = new MulticastSocket(port + 1)) {
			receiveSocket.setNetworkInterface(nic);
			receiveSocket.setSoTimeout(timeout);
			receiveSocket.joinGroup(multicastAddress);

			sendSocket.setNetworkInterface(nic);
			sendSocket.setSoTimeout(timeout);
			sendSocket.setTimeToLive(0); // TTL=0 == "only on same host"

			final byte[] sendBuf = new byte[20];
			sendSocket.send(new DatagramPacket(sendBuf, sendBuf.length, multicastAddress, port));

			final byte[] rcvBuf = new byte[20];
			receiveSocket.receive(new DatagramPacket(rcvBuf, rcvBuf.length));

			// If we reach here, it worked. Otherwise, the above code will throw.
		}
	}
}
