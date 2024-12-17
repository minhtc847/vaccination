import React, { useState, useEffect } from 'react'
import { Row, Card, Image, Col, Collapse } from 'react-bootstrap';
import { useLocation } from 'react-router-dom';
import adminItems from "../data/adminSidebar.json";
import employeeItems from "../data/employeeSidebar.json"
import customerItems from "../data/customerSidebar.json"
import SidebarItem from './SidebarItem';
import { UserInfo } from './Interface/BusinessObjectInterface';


function Sidebar() {
    const [open, setOpen] = useState(false);
    const location = useLocation();
    const currentPath = location.pathname;
    const token = localStorage.getItem('token');
    const userInfoString = localStorage.getItem('userInfo');
    const userInfo: UserInfo | null = userInfoString ? JSON.parse(userInfoString) : null;


    useEffect(() => {
        // Set open to true if token exists
        if (token) {
            setOpen(true);
        } else {
            setOpen(false);
        }
    }, [token]);

    const sidebarItems = userInfo?.role === "ROLE_ADMIN" ? adminItems :
        userInfo?.role === "ROLE_EMPLOYEE" ? employeeItems : customerItems;

    return (
        <div className="sidebar">
            <div className="user-profile-container">
                {token ?
                    <Row className={open ? "user-profile open" : "user-profile"}>
                        <Col className='profile-card'>
                            <Image src={userInfo?.image ? userInfo?.image : "/assets/manager.png"} roundedCircle />
                            <Card.Body>
                                <Col>
                                    <Card.Title style={{ textAlign: 'start' }}>
                                        {userInfo?.username && userInfo.username.length > 17
                                            ? `${userInfo.username.substring(0, 17)}...`
                                            : userInfo?.username
                                        }
                                    </Card.Title>
                                    <Card.Text style={{ textAlign: 'start' }}>
                                        {userInfo?.email}
                                    </Card.Text>
                                </Col>

                            </Card.Body>
                        </Col>
                        <Col className="icon">
                            <i className="fa-solid fa-chevron-up toggle-btn" onClick={() => setOpen(!open)} aria-controls='content' aria-expanded={open} ></i>
                        </Col>
                    </Row> :
                    <Row>
                    </Row>
                }
            </div>
            <Collapse in={open}>
                <div id='content'>
                    {sidebarItems.map((item, index) => (
                        <SidebarItem
                            key={index}
                            item={item}
                            currentPath={currentPath}
                        />
                    ))}
                </div>
            </Collapse>
        </div>
    );

}

export default Sidebar