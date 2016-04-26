/*******************************************************************************
 * Copyright (c) 2016 Daniel Murygin <dm{a}sernet{dot}de>.
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Daniel Murygin <dm{a}sernet{dot}de> - initial API and implementation
 ******************************************************************************/
package sernet.verinice.rcp.linktable;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.EditorPart;

import sernet.gs.ui.rcp.main.Activator;
import sernet.gs.ui.rcp.main.ImageCache;
import sernet.gs.ui.rcp.main.service.ServiceFactory;
import sernet.verinice.rcp.linktable.composite.VeriniceLinkTableComposite;
import sernet.verinice.rcp.linktable.composite.VeriniceLinkTableFieldListener;
import sernet.verinice.service.linktable.vlt.VeriniceLinkTable;
import sernet.verinice.service.linktable.vlt.VeriniceLinkTableIO;

/**
 *
 *
 * @author Daniel Murygin <dm{a}sernet{dot}de>
 */
public class VeriniceLinkTableEditor extends EditorPart {

    public static final String EDITOR_ID = VeriniceLinkTableEditor.class.getName();

    private static final Logger LOG = Logger.getLogger(VeriniceLinkTableEditor.class);
    private VeriniceLinkTable veriniceLinkTable;
    private boolean isDirty = false;

    private VeriniceLinkTableFieldListener contentObserver;
    private String toolTip = null;
    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        if (! (input instanceof VeriniceLinkTableEditorInput)) {
            throw new PartInitException("Input is not an instance of " + VeriniceLinkTableEditorInput.class.getSimpleName()); //$NON-NLS-1$
        }

        VeriniceLinkTableEditorInput vltEditorInput = (VeriniceLinkTableEditorInput) input;
        veriniceLinkTable=vltEditorInput.getInput();

        setSite(site);
        setInput(vltEditorInput);
        setPartName(veriniceLinkTable.getName());
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {

        Activator.inheritVeriniceContextState();
        createEditor(parent);
    }

    public void createEditor(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);

        Composite buttonContainer = new Composite(container, SWT.NONE);
        Button exportButton = new Button(buttonContainer, SWT.PUSH);
        exportButton.setText(Messages.VeriniceLinkTableEditor_1);
        exportButton.setToolTipText(Messages.VeriniceLinkTableEditor_2);
        GridDataFactory.swtDefaults().applyTo(exportButton);

        exportButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {


                ExportLinkTableHandler exportHandler = new ExportLinkTableHandler(true,
                        veriniceLinkTable);
                try {
                    exportHandler.execute(new ExecutionEvent());
                } catch (ExecutionException e) {
                    LOG.error("Error while exporting data", e);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        VeriniceLinkTableComposite ltr = new VeriniceLinkTableComposite(veriniceLinkTable,
                ServiceFactory.lookupObjectModelService(),
                container,
                SWT.NONE);

        contentObserver = new VeriniceLinkTableFieldListener() {

            @Override
            public void fieldValueChanged(boolean isValid) {
                if (isValid) {
                    Image defaultImage = ImageCache.getInstance().getImage(ImageCache.TOOL);
                    setTitleImage(defaultImage);
                    setPartName(veriniceLinkTable.getName());
                    toolTip = getPartName();
                } else {
                    Image warningImage = ImageCache.getInstance().getImage(ImageCache.WARNING);
                    setTitleImage(warningImage);
                    setPartName("INVALID_QUERY");
                    toolTip = "The query is not valid and therefore cannot be saved.";

                }
                isDirty = isValid;
                firePropertyChange(IEditorPart.PROP_DIRTY);

            }
        };
        ltr.addListener(contentObserver);

        GridLayoutFactory.swtDefaults().generateLayout(ltr);
        GridLayoutFactory.swtDefaults().extendedMargins(9, 0, 30, 0)
                .generateLayout(buttonContainer);
        GridLayoutFactory.swtDefaults().generateLayout(container);
        contentObserver.fieldValueChanged(
                VeriniceLinkTableUtil.isValidVeriniceLinkTable(veriniceLinkTable));
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
        String filePath = getFilePath(veriniceLinkTable);
        if (filePath != null) {
            executeSave(filePath);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#getTitleToolTip()
     */
    @Override
    public String getTitleToolTip() {
        return toolTip == null? getPartName():toolTip;

    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        String vltFilePath = getFilePath(veriniceLinkTable);
        if (vltFilePath != null) {
            veriniceLinkTable.setNewId();
            String name = vltFilePath.substring(vltFilePath.lastIndexOf(File.separator) + 1,
                    vltFilePath.length() - 1);
            veriniceLinkTable.setName(name);
            executeSave(vltFilePath);
        }
    }
    
    private void executeSave(String filePath) {
        VeriniceLinkTableIO.write(veriniceLinkTable, filePath);
        isDirty = false;
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    private String getFilePath(VeriniceLinkTable veriniceLinkTable) { 
        String filePath = LinkTableFileRegistry.getFilePath(veriniceLinkTable.getId());
        if(filePath==null) {
            filePath = VeriniceLinkTableUtil.createVltFilePath(
                    Display.getCurrent().getActiveShell(), Messages.VeriniceLinkTableEditor_4);
            if (filePath != null) {
                veriniceLinkTable.setName(new File(filePath).getName());
                setPartName(veriniceLinkTable.getName());
                LinkTableFileRegistry.add(veriniceLinkTable.getId(), filePath);
            }
        }
        return filePath;
    }  

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return isDirty;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        // nothing to do

    }

}
