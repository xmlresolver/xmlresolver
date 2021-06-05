import org.gradle.api.GradleException

public abstract class Docker {
  private static final String pathSep = System.getProperty("path.separator")
  private static final String fileSep = System.getProperty("file.separator")
  private static final String dockerFn = "docker"
  private static final String dockerComposeFn = "docker-compose"

  private static boolean searchedPath = false
  private static String dockerExec = null
  private static String dockerComposeExec = null

  static String docker() {
    searchPath()
    return dockerExec
  }

  static String dockerCompose() {
    searchPath()
    return dockerComposeExec
  }

  static boolean running(String name) {
    return containers().containsKey(name)
  }

  static void assertDockerAvailable() {
    searchPath()
    if (dockerExec == null) {
      throw new GradleException("Docker is unavailable (not on PATH: ${dockerFn})")
    }
  }

  static void assertDockerComposeAvailable() {
    searchPath()
    if (dockerComposeExec == null) {
      throw new GradleException("Docker-compose is unavailable (not on PATH: ${dockerComposeFn})")
    }
  }

  static void assertRunning(String name) {
    if (!running(name)) {
      throw new GradleException("Docker container is not running: ${name}")
    }
  }

  static void assertNotRunning(String name) {
    if (running(name)) {
      throw new GradleException("Docker container is running: ${name}")
    }
  }

  static HashMap<String,String> containers() {
    HashMap<String,String> cmap = new HashMap<String,String> ()

    searchPath()
    if (dockerExec == null) {
      return cmap
    }

    ProcessBuilder pb = new ProcessBuilder()
      .command(dockerExec, "ps", "-a", "--format={{.ID}} {{.Names}} {{.Status}}")
    Process proc = pb.start();
    proc.waitFor()

    // I don't know how much buffering Process does, but I don't expect the
    // output from the docker command to be very large, so I'm just going
    // to assume it'll all be ok.
    BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))
    String line = reader.readLine()
    while (line != null) {
      def parts = line.split(" ")
      if (parts.length > 2) {
        def id = parts[0]
        def name = parts[1]
        def status = parts[2]

        if (name.contains(":")) {
          name = name.split(":")[0];
        }
      
        if (!status.startsWith("Exited")) {
          cmap.put(name, id)
        }
      }

      line = reader.readLine();
    }
    reader.close()
    return cmap
  }

  private static searchPath() {
    if (searchedPath) {
      return
    }
    
    searchedPath = true

    System.getenv("PATH").split(pathSep).each { dir ->
      if (dockerExec == null) {
        File fn = new File("${dir}${fileSep}${dockerFn}")
        if (fn.exists() && fn.canExecute()) {
          dockerExec = fn.getAbsolutePath()
        } else {
          fn = new File("${dir}${fileSep}${dockerFn}.exe")
          if (fn.exists() && fn.canExecute()) {
            dockerExec = fn.getAbsolutePath()
          }
        }
      }

      if (dockerComposeExec == null) {
        File fn = new File("${dir}${fileSep}${dockerComposeFn}")
        if (fn.exists() && fn.canExecute()) {
          dockerComposeExec = fn.getAbsolutePath()
        } else {
          fn = new File("${dir}${fileSep}${dockerComposeFn}.exe")
          if (fn.exists() && fn.canExecute()) {
            dockerComposeExec = fn.getAbsolutePath()
          }
        }
      }
    }
  }
}
