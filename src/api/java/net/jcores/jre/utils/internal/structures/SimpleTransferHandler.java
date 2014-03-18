package net.jcores.jre.utils.internal.structures;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import net.jcores.jre.options.Option;

public abstract class SimpleTransferHandler extends TransferHandler {

    /** */
    private static final long serialVersionUID = 8277548357917797155L;
    
    /**
     * @param options
     */
    public SimpleTransferHandler(Option[] options) {
        
    }

    /**
     * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
     */
    @Override
    public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
        for (int i = 0; i < arg1.length; i++) {
            DataFlavor flavor = arg1[i];
            if (flavor.equals(DataFlavor.javaFileListFlavor)) { return true; }
        }
        return false;
    }

    /**
     * Do the actual import.
     * 
     * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean importData(JComponent comp, Transferable t) {
        DataFlavor[] flavors = t.getTransferDataFlavors();
        for (int i = 0; i < flavors.length; i++) {
            DataFlavor flavor = flavors[i];
            try {
                if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                    files((List<File>) t.getTransferData(DataFlavor.javaFileListFlavor));
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public abstract void files(List<File> files);
}
