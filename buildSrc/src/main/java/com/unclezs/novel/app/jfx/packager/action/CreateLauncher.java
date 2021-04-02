package com.unclezs.novel.app.jfx.packager.action;

import cn.hutool.core.bean.BeanUtil;
import com.unclezs.jfx.launcher.Manifest;
import com.unclezs.novel.app.jfx.packager.model.LauncherConfig;
import com.unclezs.novel.app.jfx.packager.subtask.BaseSubTask;
import com.unclezs.novel.app.jfx.packager.util.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;

/**
 * @author blog.unclezs.com
 * @date 2021/04/02 11:04
 */
public class CreateLauncher extends BaseSubTask {

  public static final String LAUNCHER = "launcher";
  private final LauncherConfig launcher;
  private File launcherJar;
  private final File outDir;


  public CreateLauncher() {
    super("创建Launcher");
    this.launcher = packager.getLauncher();
    this.outDir = new File(packager.getOutputDir(), LAUNCHER);
  }

  @Override
  protected boolean enabled() {
    return packager.userLauncher();
  }

  @Override
  protected File run() throws Exception {
    Dependency dependency = project.getDependencies().create(launcher.getCoordinate());
    Configuration launcher = project.getConfigurations().maybeCreate(LAUNCHER);
    launcher.getDependencies().add(dependency);
    launcher.getResolvedConfiguration().getResolvedArtifacts().forEach(artifact -> {
      String libName = artifact.getName().concat(".").concat(artifact.getExtension());
      if (libName.contains(LAUNCHER)) {
        launcherJar = artifact.getFile();
      } else {
        // 用于生成Jre
        packager.getExtModulePath().add(new File(outDir, libName).getAbsolutePath());
      }
      project.copy(c -> c.from(artifact.getFile()).into(outDir).rename(name -> libName));
    });
    embeddedManifest();
    return launcherJar;
  }

  /**
   * 将配置文件嵌入 launcherJar
   *
   * @throws IOException /
   */
  public void embeddedManifest() throws IOException {
    Manifest manifest = BeanUtil.toBean(launcher, Manifest.class);
    Logger.info("开始嵌入manifest");
    File jar = new File(packager.getJarFileDestinationFolder(), packager.getLauncher().getLauncherJarName().concat(".jar"));
    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(jar))) {
      zos.putNextEntry(new ZipEntry(Manifest.EMBEDDED_CONFIG_NAME));
      zos.write(manifest.toJson().getBytes(StandardCharsets.UTF_8));
      ZipFile zipFile = new ZipFile(this.launcherJar);
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry zipEntry = entries.nextElement();
        if (zipEntry.getName().equals(Manifest.EMBEDDED_CONFIG_NAME)) {
          continue;
        }
        zos.putNextEntry(zipEntry);
        zos.write(zipFile.getInputStream(zipEntry).readAllBytes());
      }
      zos.closeEntry();
    }
    this.launcherJar = jar;
    Logger.info("manifest已嵌入: {}", jar);
  }
}