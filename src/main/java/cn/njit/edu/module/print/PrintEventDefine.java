package cn.njit.edu.module.print;

import cn.njit.edu.view.EventDefine;
import cn.njit.edu.view.ModuleInit;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;

public class PrintEventDefine extends EventDefine {


    private final IPrintMethod method;

    public PrintEventDefine(ModuleInit jdModuleInit, IPrintMethod method) {
        super(jdModuleInit);
        this.method = method;

    }

    public void doEventChooseAll(ToolbarItem button) {
        button.setOnClick(event -> {
            this.method.chooseAll();
        });

    }

    public void doEventClearAll(ToolbarItem button) {
        button.setOnClick(event -> {
            this.method.clearAll();
        });
    }

    public void doEventPrintTzs(ToolbarItem button) {
        button.setOnClick(event -> {
            this.method.print();
        });
    }

    public void doEventPreview(ToolbarItem button) {
        button.setOnClick(event -> {
            this.method.preview();
        });
    }
}
