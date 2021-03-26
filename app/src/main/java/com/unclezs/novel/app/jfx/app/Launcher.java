package com.unclezs.novel.app.jfx.app;

import com.unclezs.novel.app.jfx.app.ui.app.App;
import com.unclezs.novel.app.jfx.app.util.DebugUtils;
import com.unclezs.novel.app.jfx.framework.ui.appication.SsaApplication;
import com.unclezs.novel.app.jfx.framework.ui.components.icon.SvgIcon;
import java.util.Locale;
import java.util.Random;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

/***
 * _ooOoo_
 * o8888888o
 * 88" . "88
 * (| -_- |)
 *  O\ = /O
 * ___/`---'\____
 * .   ' \\| |// `.
 * / \\||| : |||// \
 * / _||||| -:- |||||- \
 * | | \\\ - /// | |
 * | \_| ''\---/'' | |
 * \ .-\__ `-` ___/-. /
 * ___`. .' /--.--\ `. . __
 * ."" '< `.___\_<|>_/___.' >'"".
 * | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * \ \ `-. \_ __\ /__ _/ .-` / /
 * ======`-.____`-.___\_____/___.-`____.-'======
 * `=---='
 * .............................................
 * 佛曰：bug泛滥，我已瘫痪！
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
@Slf4j
public class Launcher {

  public static void main(String[] args) {
    DebugUtils.init();
    SvgIcon.load("/assets/icons/svg.properties");
    Random random = new Random();
    int i = random.nextInt(3);
    if (i == 1) {
      Locale.setDefault(Locale.ENGLISH);
    } else if (i == 2) {
      Locale.setDefault(Locale.TAIWAN);
    }
    Platform.startup(() -> {
      SsaApplication app = new App();
      try {
        app.init();
        app.start();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(-1);
      }
    });
  }
}