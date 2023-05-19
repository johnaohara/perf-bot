package io.hyperfoil.tools.regressionBot.benchmark;

import java.util.ArrayList;
import java.util.List;

public class Benchmark {

    String name;
    String id;

    String repo;
    List<String> authorizedUsers;

    public Benchmark() {
        authorizedUsers = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public List<String> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public void setAuthorizedUsers(List<String> authorizedUsers) {
        this.authorizedUsers = authorizedUsers;
    }

    @Override
    public String toString() {
        return "Benchmark{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", repo='" + repo + '\'' +
                ", authorizedUsers=" + authorizedUsers +
                '}';
    }

    public class Run{
        volatile STATE state;
    }

    public enum STATE{

        RUNNING,
        FINISHED,
        FAILED
    }
}
