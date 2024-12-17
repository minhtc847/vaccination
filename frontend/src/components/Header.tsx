import React from 'react'
import {Col, Row} from 'react-bootstrap'
import Logout from './Logout';
import {Link} from "react-router-dom";

function Header() {
    const token = localStorage.getItem('token');
    const currentPath = window.location.pathname;

    // Check if current path is /forgot-password or /set-password
    const isForgotOrSetPassword = currentPath === '/forgot-password' || currentPath === '/set-password';
    return (
        <header style={{backgroundColor: "#009688"}}>
            <Row>
                <Col style={{display: 'flex', alignItems: 'center'}}>
                    <img style={{width: "100px"}} className='p-4' src="/assets/FPT_logo_2010.svg.png" alt=""/>
                </Col>
                <Col style={{display: 'flex', alignItems: 'center', justifyContent: 'end'}}>
                    {!isForgotOrSetPassword && (token ? <Logout/> :
                            <Link to={"/login"} className="text-decoration-none text-white">Login</Link>
                    )}
                    <i className='fas fa-search' style={{fontSize: '18px', color: '#fff', width: "100px"}}></i>
                </Col>
            </Row>
        </header>
    )
}

export default Header