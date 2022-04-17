package cn.njit.edu.view;

/**
 * UI 基类
 * 提供查找子child控件的函数 $
 */

public interface UIObject {

    /**
     * This method calls all the other methods, so that it can be initialized
     * easier.
     */
    default void init() {
        initializeSelf();
        initializeParts();
        layoutParts();
        setupEventHandlers();
        setupValueChangedListeners();
        setupBindings();
    }

    /**
     * This method can be used to initialize the parts of the same class.
     */
    default void initializeSelf() {}

    /**
     * This method is used to initializes all the properties of a class.
     */
    void initializeParts();

    /**
     * This method is used to align the parts of a class.
     */
    void layoutParts();

    /**
     * This method is used to set up event handlers.
     */
    default void setupEventHandlers() {}

    /**
     * This method is used to set up value change listeners.
     */
    default void setupValueChangedListeners() {}

    /**
     * This method is used to configure the bindings of the properties.
     */
    default void setupBindings() {}


}
