import React, { useState, useEffect } from 'react';
import { NavLink } from 'react-router-dom';
import Collapse from 'react-bootstrap/Collapse';
import { SidebarItemProps } from './Interface/UtilsInterface';

const SidebarItem: React.FC<SidebarItemProps> = ({ item, currentPath }) => {
    const [open, setOpen] = useState(false);

    useEffect(() => {
        if (item.childrens) {
            setOpen(item.childrens.some(child => child.path === currentPath));
        }
    }, [currentPath, item.childrens]);

    const isDescendant = (parent: SidebarItemProps['item'], path: string): boolean => {
        if (parent.path === path) return true;
        if (!parent.childrens) return false;
        return parent.childrens.some(child => isDescendant(child, path));
    };

    const isSelected = item.path === currentPath;
    const isParentSelected = item.childrens && item.childrens.some(child => child.path === currentPath || isDescendant(child, currentPath));

    if (item.childrens) {
        return (
            <div className={open ? "sidebar-item open" : "sidebar-item"}>
                <div className={`sidebar-title ${isParentSelected ? 'selected' : ''}`} onClick={() => setOpen(!open)}>
                    <span className="pointer"  aria-controls='sidebar-content' aria-expanded={open}>
                        {item.title}
                    </span>
                    <i className="fa-solid fa-plus toggle-btn" onClick={() => setOpen(!open)} />
                </div>
                <Collapse in={open}>
                    <div className="sidebar-content" id='sidebar-content'>
                        {item.childrens.map((child, index) => (
                            <NavLink key={index} to={child.path || "#"} className={`child-item ${child.path === currentPath ? 'selected' : ''}`}>
                                <div className="child-title">
                                    <span className={child.path === currentPath ? 'selected' : ''}>
                                        {child.path === currentPath && '>'} {child.title}
                                    </span>
                                </div>
                            </NavLink>
                        ))}
                    </div>
                </Collapse>
            </div>
        );
    } else {
        return (
            <NavLink to={item.path || "#"} className={`sidebar-item ${isSelected ? 'selected' : ''}`}>
                <div className="sidebar-title">
                    <span className={isSelected ? 'selected' : ''}>
                        {item.title}
                    </span>
                </div>
            </NavLink>
        );
    }
};





export default SidebarItem;

