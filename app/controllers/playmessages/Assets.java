package controllers.playmessages;

import controllers.AssetsBuilder;
import play.api.mvc.*;

public class Assets {

    public static Action<AnyContent> at(String path, String file) {
        return controllers.Assets.at(path, file);
        //return delegate.at(path, file);
    }

    private static controllers.AssetsBuilder delegate = new controllers.AssetsBuilder();
}
