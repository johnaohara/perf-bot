package io.hyperfoil.tools.regressionBot.benchmark;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Startup
@ApplicationScoped
public class Benchmarks {

    private final Map<String, Benchmark> benchmarks;

    Benchmarks() {
        benchmarks = new HashMap<>();

        try {
            List<String> resourceFiles = getResourceFiles("benchmarks");
            for (String file : resourceFiles) {
                Yaml yaml = new Yaml();
                InputStream inputStream = getResourceAsStream("benchmarks/".concat(file));

                Benchmark benchmark = yaml.loadAs(inputStream, Benchmark.class);

                benchmarks.put(benchmark.getId(), benchmark);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getBechmarkNames() {
        return benchmarks.keySet();
    }


    public Benchmark getBenchmark(String repo) {
        //TODO:: this returns a single benchmark but there might be multiple benchmarks per repo
        Set foundBenchmarks = benchmarks.entrySet().stream().filter(entry -> entry.getValue().repo.equals(repo)).collect(Collectors.toSet());

        return foundBenchmarks.size() == 0 ? null : (Benchmark) foundBenchmarks.stream().findFirst().get();
    }

    public Set<String> getBechmarksPerRepo(String repoName) {
        return benchmarks.entrySet().stream().filter(entry -> entry.getValue().repo.equals(repoName)).map(entry -> entry.getKey()).collect(Collectors.toSet());
    }

    public boolean containsRepo(String repo) {
        return getBenchmark(repo) != null;
    }

    private List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();

        try (
                InputStream in = getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }

        return filenames;
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
