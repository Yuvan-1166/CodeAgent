package com.example.aiagent;

import com.example.aiagent.cli.CLIHandler;

public class Agent {
    public static void main(String[] args) {
        CLIHandler cli = new CLIHandler();
        cli.handle(args);
    }
}
