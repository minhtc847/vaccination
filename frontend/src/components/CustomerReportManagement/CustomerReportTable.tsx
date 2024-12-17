import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button, Form } from 'react-bootstrap';
import { CustomerReport, VaccineType } from '../Interface/BusinessObjectInterface';
import Paging from '../Utils/Paging';
import { showErrorAlert } from '../Utils/ErrorAlert';
import LoadingOverlay from '../Utils/LoadingOverlay/LoadingOverlay';
import { Search } from '../Interface/UtilsInterface';
import BASE_URL from '../Api/BaseApi';

const CustomerReportTable: React.FC = () => {
    const [reports, setReports] = useState<CustomerReport[]>([]);
    const [pageNo, setPageNo] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(0);
    const [fullName, setFullName] = useState<string>("");
    const [dateFrom, setDateFrom] = useState<string>("");
    const [dateTo, setDateTo] = useState<string>("");
    const [address, setAddress] = useState<string>("");
    const pageSize: number = 5;
    const [totalElements, setTotalElement] = useState<number>(0);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [searchParams, setSearchParams] = useState({ fullName: "", dateFrom: "", dateTo: "", address: "" });
    const nav = useNavigate();
    const token = localStorage.getItem("token");
    document.title = "Report Customer";

    useEffect(() => {
        setIsLoading(true);
        setTimeout(() => {
            axios.get(`${BASE_URL}/vaccination/customerreport`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                params: {
                    pageNo: pageNo,
                    pageSize: pageSize,
                    sortBy: 'id',
                    sortDir: 'asc',
                    ...searchParams
                },
            })
                .then(response => {
                    const result: Search<CustomerReport> = response.data;
                    setReports(result.content);
                    setTotalPages(result.totalPaged);
                    setTotalElement(result.totalElements);

                })
                .catch(error => {
                    if (error.response) {
                        showErrorAlert(() => {
                            nav("/");
                        }, error.response.data.message)
                    }
                })
                .finally(() => {
                    setIsLoading(false);
                })
        }, 400)
    }, [token, pageNo, searchParams, nav]);

    const handlePageChange = (pageNumber: number) => {
        setPageNo(pageNumber - 1);
    };

    const handleFilter = () => {
        setPageNo(0);
        setSearchParams({ fullName, dateFrom, dateTo, address });
    };

    const handleReset = () => {
        setFullName("");
        setDateFrom("");
        setDateTo("");
        setAddress("");
        setSearchParams({ fullName: "", dateFrom: "", dateTo: "", address: "" });
        setPageNo(0);
    };

    return (
        <Container className='pt-4'>
            {isLoading ? <LoadingOverlay /> : null}
            <h5 className='pb-3' style={{ fontWeight: "bold" }}>REPORT CUSTOMER</h5>
            <Container style={{ backgroundColor: "white" }}>
                <Form>
                    <Row className='py-3' style={{ borderBottom: '2px solid whitesmoke' }}>
                        <Col style={{ textAlign: 'start' }} lg={2}>
                            <Form.Label htmlFor='displayType'>Display Type:</Form.Label>
                            <div className="mb-3" style={{ display: 'flex', alignItems: 'center', paddingTop: '5px' }}>
                                <Form.Check
                                    inline
                                    label="Report"
                                    id='displayTypeReport'
                                    type="radio"
                                    checked
                                    style={{ marginRight: '10px' }}
                                />
                                <Form.Check
                                    inline
                                    label="Chart"
                                    id="displayTypeChart"
                                    type="radio"
                                    onChange={() => nav("/report/customer/chart")}
                                />
                            </div>
                        </Col>
                        <Col style={{ textAlign: 'start' }} lg={4}>
                            <Form.Label>Date of Birth:</Form.Label>
                            <div className="mb-3" style={{ display: 'flex', alignItems: 'center' }}>
                                <Form.Control id='from' type="date" value={dateFrom}
                                    onChange={(e) => setDateFrom(e.target.value)} style={{ marginRight: '10px' }} />
                                <span style={{ marginRight: '10px' }}>To</span>
                                <Form.Control id='to' type="date" value={dateTo}
                                    onChange={(e) => setDateTo(e.target.value)} />
                            </div>
                        </Col>
                        <Col style={{ textAlign: 'start' }} lg={2}>
                            <Form.Label>Full name:</Form.Label>
                            <div className="mb-3">
                                <Form.Control
                                    type='text'
                                    value={fullName}
                                    onChange={(e) => setFullName(e.target.value)}
                                />
                            </div>
                        </Col>
                        <Col style={{ textAlign: 'start' }} lg={2}>
                            <Form.Label>Address:</Form.Label>
                            <div className="mb-3">
                                <Form.Control
                                    type='text'
                                    value={address}
                                    onChange={(e) => setAddress(e.target.value)}
                                />
                            </div>
                        </Col>
                        <Col style={{ textAlign: 'start' }} lg={2}>
                            <Form.Label>Action:</Form.Label>
                            <div className="mb-3" style={{ display: 'flex', justifyContent: 'start' }}>
                                <Button style={{ backgroundColor: '#009688', borderColor: '#009688', marginRight: '5px' }} onClick={handleReset}>Reset</Button>
                                <Button style={{ backgroundColor: '#009688', borderColor: '#009688' }} onClick={handleFilter}>Filter</Button>
                            </div>
                        </Col>
                    </Row>
                </Form>
                <Container as={Row} className='py-3'>
                    {reports.length > 0 ?
                        <table>
                            <thead>
                                <tr>
                                    <th>No.</th>
                                    <th>Full name</th>
                                    <th>Date Of Birth</th>
                                    <th>Address</th>
                                    <th>Identity card</th>
                                    <th>Number of Inject</th>
                                </tr>
                            </thead>
                            <tbody className='text-start'>
                                {reports.map((r, index) => (
                                    <tr key={index}>
                                        <td>{pageNo * pageSize + index + 1}</td>
                                        <td>{r.employeeName}</td>
                                        <td>{r.dateOfBirth}</td>
                                        <td>{r.address}</td>
                                        <td>{r.identityCard}</td>
                                        <td>{r.numberOfInject}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        : <h3>No data found!</h3>}
                </Container>
                <Row className='py-3'>
                    <Col style={{ textAlign: 'start' }}>Showing {pageNo * pageSize + 1} to {(pageNo + 1) * pageSize} of {totalElements} entries</Col>
                    <Col style={{ textAlign: 'end' }}>
                        <Paging currentPage={pageNo} totalPages={totalPages} onPageChange={handlePageChange} />
                    </Col>
                </Row>
            </Container>
        </Container>

    )
}

export default CustomerReportTable