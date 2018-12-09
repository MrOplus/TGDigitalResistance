package com.github.kooroshh.tgdigitalresistance;

import java.util.List;

/**
 * Created by Oplus on 2018/05/03.
 */

public interface IResistanceServerResult {
    void onResistanceServerAvailable(List<ResistanceServer> servers);
    void onResistanceServerNotAvailable();
}
