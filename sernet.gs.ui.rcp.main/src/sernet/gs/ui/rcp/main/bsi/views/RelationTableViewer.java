/*******************************************************************************
 * Copyright (c) 2010 Alexander Koderman <ak[at]sernet[dot]de>.
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 *     This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 *     You should have received a copy of the GNU Lesser General Public 
 * License along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Alexander Koderman <ak[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package sernet.gs.ui.rcp.main.bsi.views;

import java.awt.FontMetrics;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import sernet.gs.service.RetrieveInfo;
import sernet.gs.ui.rcp.main.service.ServiceFactory;
import sernet.verinice.interfaces.CommandException;
import sernet.verinice.model.common.CnALink;
import sernet.verinice.model.common.CnATreeElement;
import sernet.verinice.model.iso27k.IISO27kElement;
import sernet.verinice.service.commands.LoadAncestors;

/**
 * @author koderman[at]sernet[dot]de
 * @version $Rev$ $LastChangedDate$ $LastChangedBy$
 * 
 */
public class RelationTableViewer extends TableViewer {

    private static final Logger LOG = Logger.getLogger(RelationTableViewer.class);

    private TableViewerColumn col6;
    private TableViewerColumn col7;
    private TableViewerColumn col8;

    final TableColumn col1;
    final TableViewerColumn viewerCol2;
    final TableViewerColumn col4;
    final IRelationTable view;
    final TableColumn col3;
    final TableViewerColumn viewerCol5;
    final TableViewerColumn col5;

    /**
     * @param parent
     * @param i
     */
    public RelationTableViewer(IRelationTable relationView, Composite parent, int style, boolean showRisk) {
        super(parent, style);

        final int defaultColumnWidth = 25;
        final int viewerCol2Width = 100;
        final int col4Width = 150;
        final int col5Width = 100;
        final int viewerCol5Width = 250;

        ColumnViewerToolTipSupport.enableFor(this, ToolTip.NO_RECREATE);

        view = relationView;

        Table table = getTable();

        // relation icon:
        col1 = new TableColumn(table, SWT.LEFT);
        col1.setText(""); //$NON-NLS-1$
        col1.setWidth(defaultColumnWidth);
        col1.setResizable(false);

        // name of relation: (i.e. "author of")
        viewerCol2 = new TableViewerColumn(this, SWT.LEFT);
        viewerCol2.getColumn().setText(Messages.RelationTableViewer_1);
        viewerCol2.getColumn().setWidth(viewerCol2Width);

        viewerCol2.setEditingSupport(new RelationTypeEditingSupport(view, this));

        // element type icon:
        col3 = new TableColumn(table, SWT.CENTER);
        col3.setText(""); //$NON-NLS-1$
        col3.setWidth(defaultColumnWidth);

        // element title
        col4 = new TableViewerColumn(this, SWT.LEFT);
        col4.getColumn().setText(Messages.RelationTableViewer_6);
        col4.getColumn().setWidth(col4Width);

        // element scope id:
        col5 = new TableViewerColumn(this, SWT.LEFT);
        col5.getColumn().setText(Messages.RelationTableViewer_5);
        col5.getColumn().setWidth(col5Width);

        viewerCol5 = new TableViewerColumn(this, SWT.LEFT);
        viewerCol5.getColumn().setText(Messages.RelationTableViewer_7);
        viewerCol5.getColumn().setWidth(viewerCol5Width);
        viewerCol5.setEditingSupport(new RelationDescriptionEditingSupport(view, this));

        // risk avalues if requested:
        if (showRisk) {
            col6 = new TableViewerColumn(this, SWT.LEFT);
            col6.getColumn().setText("C"); //$NON-NLS-1$
            col6.getColumn().setWidth(defaultColumnWidth);

            col7 = new TableViewerColumn(this, SWT.LEFT);
            col7.getColumn().setText("I"); //$NON-NLS-1$
            col7.getColumn().setWidth(defaultColumnWidth);

            col8 = new TableViewerColumn(this, SWT.LEFT);
            col8.getColumn().setText("A"); //$NON-NLS-1$
            col8.getColumn().setWidth(defaultColumnWidth);
        }

        setColumnProperties(new String[] { IRelationTable.COLUMN_IMG, //$NON-NLS-1$
                IRelationTable.COLUMN_TYPE, //$NON-NLS-1$
                IRelationTable.COLUMN_TYPE_IMG, //$NON-NLS-1$
                IRelationTable.COLUMN_SCOPE_ID, //$NON-NLS-1$
                IRelationTable.COLUMN_TITLE, //$NON-NLS-1$
                IRelationTable.COLUMN_COMMENT, //$NON-NLS-1$
                IRelationTable.COLUMN_RISK_C, //$NON-NLS-1$
                IRelationTable.COLUMN_RISK_I, //$NON-NLS-1$
                IRelationTable.COLUMN_RISK_A //$NON-NLS-1$
        });

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

    }

    /**
     * Provides an object path of the linked object in the title column in the
     * relation view.
     */
    class RelationTableCellLabelProvider extends CellLabelProvider {

        /**
         * Caches the object pathes. Key is the title of the target
         * CnaTreeElement which is listed in the title column
         */
        Map<CnALink.Id, String> cache;

        /** the current width of the verinice window */
        int shellWidth;

        /** current x value of the verinice window */
        int shellX;

        /** current font used in verinice */
        Font font;

        RelationViewLabelProvider relationViewLabelProvider;

        public RelationTableCellLabelProvider(RelationViewLabelProvider relationViewLabelProvider, Font font) {
            this.relationViewLabelProvider = relationViewLabelProvider;
            this.font = font;
            cache = new HashMap<CnALink.Id, String>();
        }

        @Override
        public String getToolTipText(final Object element) {

            if (!(element instanceof CnALink)) {
                return "";
            }

            CnALink link = (CnALink) element;

            // the mouse location must be tracked, before we make "slow" remote
            // calls"
            int mouseX = MouseInfo.getPointerInfo().getLocation().x;
            LOG.debug("mouse location: " + mouseX);

            if (cache.containsKey(link.getId())) {
                return cropToolTip(cache.get(link.getId()), mouseX);
            }

            try {

                RetrieveInfo ri = RetrieveInfo.getPropertyInstance();
                relationViewLabelProvider.replaceLinkEntities(link);

                CnATreeElement cnATreeElement = null;
                if (CnALink.isDownwardLink(relationViewLabelProvider.getInputElemt(), link)) {
                    cnATreeElement = link.getDependency();
                } else {
                    cnATreeElement = link.getDependant();
                }

                LoadAncestors command = new LoadAncestors(cnATreeElement.getTypeId(), cnATreeElement.getUuid(), ri);
                command = ServiceFactory.lookupCommandService().executeCommand(command);
                CnATreeElement current = command.getElement();

                // build object path
                StringBuilder sb = new StringBuilder();
                sb.insert(0, current.getTitle());

                while (current.getParent() != null) {
                    current = current.getParent();
                    sb.insert(0, "/");
                    sb.insert(0, current.getTitle());
                }

                // crop the root element, which is always ISO .. or BSI ...
                Path p = Paths.get(sb.toString());
                p = p.subpath(1, p.getNameCount());

                // FIXME use EHCache
                cache.put(link.getId(), p.toString());

            } catch (CommandException e) {
                LOG.debug("loading ancestors failed", e);
            }

            return cropToolTip(cache.get(link.getId()), mouseX);
        }

        /**
         * Since rcp does not display strings, which are wider than the
         * {@link Shell} width, we have to calculate the font metric and maybe
         * to crop the string.
         * 
         * @param toolTipText
         *            the raw tooltiptext with the complete path.
         * 
         * @param mouseX
         *            current x value of the mouse position on the whole
         *            display, multi displays are taken into account
         * 
         * @return if the tooltip text is wider the {@link Shell} elements from
         *         the end are removed until the text is short enough.
         */
        public String cropToolTip(String toolTipText, int mouseX) {

            FontData[] fontData = font.getFontData();

            // take first font
            if (fontData.length > 0) {
                FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(new java.awt.Font(fontData[0].name, fontData[0].style, fontData[0].getHeight()));
                int width = fontMetrics.stringWidth(toolTipText);

                // crop
                int spaceLeft = shellWidth - (Math.abs(shellX - mouseX));

                // FIXME the 200 pixel are in magic number. The calculation of
                // the string width seems to be too optimistic
                if (width >= spaceLeft - 200) {
                    Path p = Paths.get(toolTipText);

                    // check again, if short enough
                    return cropToolTip(p.getParent().toString() + " ...", mouseX);
                }
            }

            return toolTipText;
        }

        @Override
        public void update(ViewerCell cell) {
            // delegate the cell text to the origin label provider
            cell.setText(relationViewLabelProvider.getColumnText(cell.getElement(), 3));

        }

        public void updateShellWidthAndX(int shellWidth, int shellX) {
            this.shellWidth = shellWidth;
            this.shellX = shellX;
        }

        @Override
        public int getToolTipTimeDisplayed(Object object) {
            return 100000;
        }

    }

    public RelationTableCellLabelProvider initToolTips(RelationViewLabelProvider relationViewLabelProvider, Font font) {
        RelationTableCellLabelProvider labelProvider = new RelationTableCellLabelProvider(relationViewLabelProvider, font);
        col4.setLabelProvider(labelProvider);
        return labelProvider;
    }
}