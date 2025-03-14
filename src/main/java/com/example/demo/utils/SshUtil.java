package com.example.demo.utils;

import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.common.channel.Channel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class SshUtil {

    /**
     * Logs into a remote host via SSH and returns a connected session.
     *
     * @param ip       IP address of the host to connect to
     * @param userName Username for authentication
     * @param password Password for authentication
     * @return ClientSession object if login is successful; null otherwise
     */
    public static ClientSession login(String ip, String userName, String password) {
        SshClient client = SshClient.setUpDefaultClient();
        client.start();
        ClientSession session = null;

        try {
            session = client.connect(userName, ip, 22)
                    .verify(10, TimeUnit.SECONDS).getSession();
            session.addPasswordIdentity(password);
            session.auth().verify(10, TimeUnit.SECONDS);
        } catch (IOException e) {
            e.printStackTrace();
            if (session != null) {
                try {
                    session.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

        return session;
    }

    /**
     * Executes a shell command on a remote host via SSH.
     *
     * @param session Established SSH session
     * @param cmd     Command to execute on the remote host
     * @return The output of the executed command as a String
     */
    public static String execute(ClientSession session, String cmd) {
        String result = "";

        try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
             ClientChannel channel = session.createChannel(Channel.CHANNEL_EXEC, cmd)) {

            channel.setOut(responseStream);
            channel.open().verify(10, TimeUnit.SECONDS);

            // Wait for the channel to close with a timeout
            channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(10));

            // 将 responseStream 的内容转换为字符串并赋值给 result
            result = responseStream.toString("UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Establishes an SSH connection, executes commands to retrieve file information,
     * and parses the results into a 2D list.
     *
     * @return A 2D ArrayList containing file information with each inner list representing a file's details (filename, size, and timestamp)
     */
    public static ArrayList<ArrayList<String>> startConnect() {
        String ip = "192.168.1.8";
        String userName = "aling";
        String password = "Sky061104";

        // Establish an SSH connection
        ClientSession session = SshUtil.login(ip, userName, password);

        if (session == null) {
            System.out.println("Failed to connect.");
            return new ArrayList<>();
        }

        // Commands to list filenames, sizes, and timestamps in a directory
        String cmd = "ls -t /home/aling/records"; // List files by time
        String cmd2 = "cd /home/aling/records && ls -lt | awk '{print $5}'"; // Get file sizes
        String cmd3 = "cd /home/aling/records && ls -lt | awk '{printf \"%s %s %s\\n\", $6,$7,$8}'"; // Get timestamps

        // Execute each command and capture the output
        String result = SshUtil.execute(session, cmd);      // List of filenames
        String result2 = SshUtil.execute(session, cmd2);     // List of file sizes
        String result3 = SshUtil.execute(session, cmd3);     // List of file timestamps
        System.out.println("1" + result + result2 + result3);

        // Split command output by newline to create arrays of data
        String[] temp = result.split("\n");
        String[] temp2 = result2.split("\n");
        String[] temp3 = result3.split("\n");
        // 2D list to store details of each file (filename, size, timestamp)
        ArrayList<ArrayList<String>> lines = new ArrayList<>();

        // Loop through each file and compile its details into the list
        for (int i = 0; i < temp.length; i++) {
            ArrayList<String> line = new ArrayList<>();

            line.add(temp[i]);              // Add filename
            line.add(temp2[i + 1]);         // Add file size (skip first line if needed)
            line.add(temp3[i + 1]);         // Add timestamp (skip first line if needed)
            lines.add(line);                // Add compiled line to the main list
        }

        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
