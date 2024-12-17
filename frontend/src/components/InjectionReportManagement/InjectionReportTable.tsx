import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button, Form } from 'react-bootstrap';
import { InjectionResultReport, VaccineType } from '../Interface/BusinessObjectInterface';
import Paging from '../Utils/Paging';
import { showErrorAlert } from '../Utils/ErrorAlert';
import LoadingOverlay from '../Utils/LoadingOverlay/LoadingOverlay';
import { Search } from '../Interface/UtilsInterface';
import './InjectionReportTable.css';
import BASE_URL from '../Api/BaseApi';

const InjectionReportTable: React.FC = () => {
    const [reports, setReports] = useState<InjectionResultReport[]>([]);
    const [pageNo, setPageNo] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(0);
    const [usage, setUsage] = useState<string>("");
    const [dateFrom, setDateFrom] = useState<string>("");
    const [dateTo, setDateTo] = useState<string>("");
    const [vaccineType, setVaccineType] = useState<string>("");
    const [vaccineTypes, setVaccineTypes] = useState<string[]>([]);
    const pageSize: number = 5;
    const [totalElements, setTotalElement] = useState<number>(0);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [searchParams, setSearchParams] = useState({ usage: "", dateFrom: "", dateTo: "", vaccineType: "" });
    const nav = useNavigate();
    const token = localStorage.getItem("token");
    document.title = "Report Injection Result";

    useEffect(() => {
        setIsLoading(true);
        setTimeout(() => {


            axios.get(`${BASE_URL}/vaccination/injectionreport`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                params: {
                    pageNo: pageNo,
                    pageSize: pageSize,
                    sortBy: 'id',
                    sortDir: 'desc',
                    ...searchParams
                },
            })
                .then(response => {
                    const result: Search<InjectionResultReport> = response.data;
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

    useEffect(() => {
        axios.get(`${BASE_URL}/vaccination/vaccinetype`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
            params: {
                pageNo: 0,
                pageSize: 1000,
                sortBy: 'id',
                sortDir: 'asc',
            },
        })
            .then(response => {
                const result: Search<VaccineType> = response.data;
                const vaccineNames = Array.from(new Set(result.content.map(vt => vt.vaccineTypeName)));
                setVaccineTypes(vaccineNames);
            })
            .catch(error => {
                if (error.response) {
                    showErrorAlert(() => {
                        nav("/");
                    }, error.response.data.message)
                }
            })
    }, [token, nav]);

    const handlePageChange = (pageNumber: number) => {
        setPageNo(pageNumber - 1);
    };

    const handleFilter = () => {
        setPageNo(0);
        setSearchParams({ usage, dateFrom, dateTo, vaccineType });
    };

    const handleReset = () => {
        setUsage("");
        setDateFrom("");
        setDateTo("");
        setVaccineType("");
        setSearchParams({ usage: "", dateFrom: "", dateTo: "", vaccineType: "" });
        setPageNo(0);
    };

    return (
        <Container className='pt-4'>
            {isLoading ? <LoadingOverlay /> : null}
            <h5 className='pb-3' style={{ fontWeight: "bold" }}>REPORT INJECTION RESULT</h5>
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
                                    onChange={() => nav("/report/injection_result/chart")}
                                />
                            </div>
                        </Col>
                        <Col style={{ textAlign: 'start' }} lg={4}>
                            <Form.Label style={{ marginRight: '10px' }}>Inject Date:</Form.Label>
                            <div className="mb-3" style={{ display: 'flex', alignItems: 'center' }}>
                                <Form.Control id='from' type="date" value={dateFrom}
                                    onChange={(e) => setDateFrom(e.target.value)} style={{ marginRight: '10px' }} />
                                <span style={{ marginRight: '10px' }}>To</span>
                                <Form.Control id='to' type="date" value={dateTo}
                                    onChange={(e) => setDateTo(e.target.value)} />
                            </div>
                        </Col>
                        <Col style={{ textAlign: 'start' }} lg={2}>
                            <Form.Label>Prevention</Form.Label>
                            <div className="mb-3">
                                <Form.Control
                                    type='text'
                                    value={usage}
                                    onChange={(e) => setUsage(e.target.value)}
                                />
                            </div>
                        </Col>
                        <Col style={{ textAlign: 'start' }} lg={2}>
                            <Form.Label>Vaccine Type</Form.Label>
                            <div className="mb-3">
                                <Form.Select
                                    value={vaccineType}
                                    onChange={(e) => setVaccineType(e.target.value)}
                                >
                                    <option value="">--Select Vaccine--</option>
                                    {vaccineTypes.map((type, index) => (
                                        <option key={index} value={type}>{type}</option>
                                    ))}
                                </Form.Select>
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
                                    <th>Vaccine</th>
                                    <th>Prevention</th>
                                    <th>Customer name</th>
                                    <th>Date of Inject</th>
                                </tr>
                            </thead>
                            <tbody className='text-start'>
                                {reports.map((r, index) => (
                                    <tr key={index}>
                                        <td>{pageNo * pageSize + index + 1}</td>
                                        <td>{r.vaccineName}</td>
                                        <td>{r.usage}</td>
                                        <td>{r.customerName}</td>
                                        <td>{r.dateOfInjection}</td>
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

export default InjectionReportTable