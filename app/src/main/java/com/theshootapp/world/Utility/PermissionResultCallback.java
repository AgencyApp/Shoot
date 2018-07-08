package com.theshootapp.world.Utility;

import java.util.ArrayList;

/**
 * Created by hamza on 03-Jul-18.
 */

public interface PermissionResultCallback {
    void PermissionGranted(int request_code);
    void PartialPermissionGranted(int request_code, ArrayList granted_permissions);
    void PermissionDenied(int request_code);
    void NeverAskAgain(int request_code);
}
