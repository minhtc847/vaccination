import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Form, Button } from 'react-bootstrap';
import { ReportChart } from '../Interface/BusinessObjectInterface';
import { showErrorAlert } from '../Utils/ErrorAlert';
import LoadingOverlay from '../Utils/LoadingOverlay/LoadingOverlay';
import { ChartData } from '../Interface/UtilsInterface';
import { BarChart } from '@mui/x-charts/BarChart';
import BASE_URL from '../Api/BaseApi';

const VaccineReportChart: React.FC = () => {
    const [chartDatas, setChartDatas] = useState<ChartData[]>([]);
    const [years, setYears] = useState<number[]>([]);
    const [selectedYear, setSelectedYear] = useState<number>(0);
    const [vaccineName, setVaccineName] = useState<string>("");
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [searchParams, setSearchParams] = useState({ vaccineName: "" });
    const nav = useNavigate();
    const token = localStorage.getItem("token");
    document.title = "Report Vaccine";

    useEffect(() => {
        axios.get(`${BASE_URL}/vaccination/vaccinereport/getAllYear`, {
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
            axios.get(`${BASE_URL}/vaccination/vaccinereport/graph`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                params: {
                    year: selectedYear,
                    ...searchParams
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
    }, [token, selectedYear, searchParams, nav])

    const handleChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        setSelectedYear(parseInt(event.target.value));
    };

    const handleFilter = () => {
        setSearchParams({ vaccineName });
    };

    const handleReset = () => {
        setVaccineName("");
        setSearchParams({ vaccineName: "" });
    };

    return (
        <Container className='pt-4'>
            {isLoading ? <LoadingOverlay /> : null}
            <h5 className='pb-3' style={{ fontWeight: "bold" }}>REPORT VACCINE</h5>
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
                                onChange={() => nav("/report/vaccine")}
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
                    <Col style={{ textAlign: 'start' }} lg={2}>
                        <Form.Label>Vaccine Name</Form.Label>
                        <div className="mb-3">
                            <Form.Control
                                type='text'
                                value={vaccineName}
                                onChange={(e) => setVaccineName(e.target.value)}
                            />
                        </div>
                    </Col>
                    <Col style={{ textAlign: 'start' }} lg={2}>
                        <Form.Label>Action:</Form.Label>
                        <div className="mb-3" style={{ display: 'flex', justifyContent: 'space-evenly' }}>
                            <Button style={{ backgroundColor: '#009688', borderColor: '#009688' }} onClick={handleReset}>Reset</Button>
                            <Button style={{ backgroundColor: '#009688', borderColor: '#009688' }} onClick={handleFilter}>Filter</Button>
                        </div>
                    </Col>
                </Row>
            </Container>
            <Container style={{ backgroundColor: "white" }}>
                <div style={{ textAlign: 'start' }} className='p-4'>REPORT VACCINE CHART</div>
                <Container className='py-3'>
                    <BarChart
                        series={[
                            { data: chartDatas.map(item => item.value), label: 'Total Vaccine' }
                        ]}
                        height={500}
                        xAxis={[{ data: chartDatas.map(item => item.month), scaleType: 'band' }]}
                    />
                </Container>
            </Container>
        </Container>
    )
}

export default VaccineReportChart