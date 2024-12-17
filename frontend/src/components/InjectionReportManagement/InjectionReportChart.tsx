import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Form } from 'react-bootstrap';
import { ReportChart } from '../Interface/BusinessObjectInterface';
import { showErrorAlert } from '../Utils/ErrorAlert';
import LoadingOverlay from '../Utils/LoadingOverlay/LoadingOverlay';
import { ChartData } from '../Interface/UtilsInterface';
import './InjectionReportTable.css';
import { BarChart } from '@mui/x-charts/BarChart';
import BASE_URL from '../Api/BaseApi';

const InjectionReportChart: React.FC = () => {
    const [chartDatas, setChartDatas] = useState<ChartData[]>([]);
    const [years, setYears] = useState<number[]>([]);
    const [selectedYear, setSelectedYear] = useState<number>(0);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const nav = useNavigate();
    const token = localStorage.getItem("token");
    document.title = "Report Injection Result";

    useEffect(() => {
        axios.get(`${BASE_URL}/vaccination/injectionreport/getAllYear`, {
            headers: {
                Authorization: `Bearer ${token}`,
            }
        })
            .then(response => {
                const result = response.data;
                setYears(result);
                setSelectedYear(result[0]);
            })
            .catch(error => {
                if (error.response) {
                    showErrorAlert(() => {
                        nav("/");
                    }, error.response.data.message)
                }
            })
    }, [token, nav])

    useEffect(() => {
        setIsLoading(true);
        setTimeout(() => {
            axios.get(`${BASE_URL}/vaccination/injectionreport/graph/${selectedYear}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            })
                .then(response => {
                    const result: ReportChart = response.data;
                    const transformedData: ChartData[] = Object.entries(result).map(([month, value]) => ({
                        month,
                        value: value as number
                    }));
                    setChartDatas(transformedData);
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
    }, [token, selectedYear, nav])

    const handleChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        setSelectedYear(parseInt(event.target.value));
    };

    return (
        <Container className='pt-4'>
            {isLoading ? <LoadingOverlay /> : null}
            <h5 className='pb-3' style={{ fontWeight: "bold" }}>REPORT INJECTION RESULT</h5>
            <Container style={{ backgroundColor: "white" }} className='mb-5'>
                <Row className='py-3'>
                    <Col style={{ textAlign: 'start' }} lg={2}>
                        <Form.Label htmlFor='displayType'>Display Type:</Form.Label>
                        <div className="mb-3" style={{ display: 'inline-flex' }}>
                            <Form.Check
                                inline
                                label="Report"
                                id='displayType'
                                type="radio"
                                onChange={() => nav("/report/injection_result")}
                            />
                            <Form.Check
                                inline
                                label="Chart"
                                id="displayType"
                                type="radio"
                                checked

                            />
                        </div>
                    </Col>
                    <Col style={{ textAlign: 'start' }} lg={2}>
                        <Form.Label>Select year:</Form.Label>
                        <div className="mb-3">
                            <Form.Select value={selectedYear} onChange={handleChange}>
                                {years.map(y => (
                                    <option key={y} value={y}>{y}</option>
                                ))}
                            </Form.Select>
                        </div>
                    </Col>
                </Row>
            </Container>
            <Container style={{ backgroundColor: "white" }}>
                <div style={{ textAlign: 'start' }} className='p-4'>REPORT INJECTION CHART</div>
                <Container className='py-3'>
                    <BarChart
                        series={[
                            { data: chartDatas.map(item => item.value), label: 'Injection Result' }
                        ]}
                        height={500}
                        xAxis={[{ data: chartDatas.map(item => item.month), scaleType: 'band' }]}
                    />
                </Container>
            </Container>
        </Container>
    )
}

export default InjectionReportChart